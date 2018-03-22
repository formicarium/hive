(ns hive.tracer.zmq
  (:require [clojure.core.async :as async]
            [hive.config :as config]
            [hive.tracer.adapters :as adapters]
            [zeromq.zmq :as zmq]))

(def context (zmq/context config/io-threads))

(defn new-router-socket! [port]
  (let [router (zmq/socket context :router)]
    (zmq/set-receive-timeout router config/socket-receive-timeout)
    (zmq/set-send-timeout router config/socket-send-timeout)
    (zmq/bind router (adapters/port->endpoint port))))

(defn receive-all-str! [socket]
  (mapv adapters/bytes->string (zmq/receive-all socket)))

(defn try-receive-message! [socket]
  (let [[ident meta payload :as received] (receive-all-str! socket)]
    (when (every? some? received)
      (adapters/raw-event->internal received))))

(defn respond! [router message]
  (async/go (zmq/send router message)))

(defn start-receiving! [router on-receive]
  (let [stop-ch      (async/chan)
        main-ch      (async/chan config/main-ch-buffer-size)]
    (async/go-loop []
      (when (async/alt! stop-ch false :default :keep-going)
        (some->> (try-receive-message! router)
                 (async/>! main-ch))
        (recur)))
    (async/go-loop []
      (when (async/alt! stop-ch false :default :keep-going)
        (some-> (async/<! main-ch)
                (on-receive router))
        (recur)))
    stop-ch))

(def terminate-receiver-channel! async/close!)
(def terminate-router-socket! zmq/close)
