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
  (let [received (receive-all-str! socket)]
    (when (every? some? received)
      (adapters/raw-event->internal received))))

(defn send! [socket & parts]
  (loop [[x & xs] parts]
    (when x
      (if xs
        (do (zmq/send socket (adapters/str->bytes x) zmq/send-more)
            (recur xs))
        (zmq/send socket (adapters/str->bytes x))))))

(defn respond! [message identity router]
  (prn "RESPONDING" identity)
  (send! router identity message))

(defn start-receiving! [router store on-receive]
  (let [stop-ch (async/chan)
        main-ch (async/chan config/main-ch-buffer-size)]
    (async/go-loop []
      (when (async/alt! stop-ch false :default :keep-going)
        (some->> (try-receive-message! router)
                 (async/>! main-ch))
        (recur)))
    (async/go-loop []
      (when (async/alt! stop-ch false :default :keep-going)
        (let [message (async/<! main-ch)]
          (async/go (on-receive message store))
          (respond! "ACK" (:identity message) router))
        (recur)))
    stop-ch))

(def terminate-receiver-channel! async/close!)
(def terminate-router-socket! zmq/close)
