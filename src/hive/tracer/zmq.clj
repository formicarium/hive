(ns hive.tracer.zmq
  (:require [zeromq.zmq :as zmq]
            [cheshire.core :as cheshire]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(def context (zmq/context 1))

(defn bytes->string [^bytes bs]
  (when bs
    (String. bs)))

(defn port->endpoint [port]
  (str "tcp://*:" port))

(defn new-router-socket! [port]
  (let [router (zmq/socket context :router)]
    (zmq/set-receive-timeout router 1000)
    (zmq/set-send-timeout router 1000)
    (zmq/bind router (port->endpoint port))))

(defn receive-all-str! [socket]
  (mapv bytes->string (zmq/receive-all socket)))

(defn receive-message! [socket]
  (let [[ident meta payload :as received] (receive-all-str! socket)]
    (when (every? some? received)
      {:identity ident
       :meta     (cheshire/parse-string meta true)
       :payload  (cheshire/parse-string payload true)})))

(defn respond! [router message]
  (go (zmq/send router message)))

(defn start-receiving! [router on-receive]
  (let [stop-channel (async/chan)
        ch (async/chan 1000)]
    (async/go-loop []
      (when (async/alt! stop-channel false :default :keep-going)
        (some->> (receive-message! router)
                 (async/>! ch))
        (recur)))
    (async/go-loop []
      (when (async/alt! stop-channel false :default :keep-going)
        (some-> (async/<! ch)
                (on-receive router))
        (recur)))
    stop-channel))

(defn terminate-receiver-channel! [ch] (async/close! ch))
(defn terminate-router-socket! [router] (zmq/close router))

