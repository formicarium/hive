(ns hive.tracer.zmq
  (:require [cheshire.core :as cheshire]
            [clj-time.core :as t]
            [clojure.core.async :as async]
            [hive.tracer.adapters :as adapters]
            [hive.config :as config]
            [hive.tracer.heartbeat :as heartbeat]
            [hive.storage.store :as store]
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
        heartbeat-ch (heartbeat/heartbeat-ch config/healthcheck-timing-s)
        main-ch      (async/chan config/main-ch-buffer-size)]
    (async/go-loop []
      (when (async/alt! stop-ch false :priority true :default :keep-going)
        (some->> (try-receive-message! router)
                 (async/>! main-ch))
        (recur)))
    (async/go-loop []
      (let [[source value] (async/alt! stop-ch [:stop]
                                       main-ch ([v] [:main v])
                                       heartbeat-ch ([v] [:heartbeat v]))]
        (case source
          :stop      (run! async/close! [stop-ch heartbeat-ch main-ch])
          :main      (do (prn "Main channel triggered.") (on-receive value router) (recur))
          :heartbeat (do (prn "Heartbeat check triggered.") (heartbeat/healthcheck-services!) (recur))
          (recur))))
    stop-ch))

(defn terminate-receiver-channel! [ch] (async/close! ch))
(defn terminate-router-socket! [router] (zmq/close router))
