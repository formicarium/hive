(ns hive.zmq
  (:require [zeromq.zmq :as zmq]
            [cheshire.core :as cheshire]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async]))

(def context (zmq/context 1))

(defn bytes->string [^bytes bs]
  (when bs
    (String. bs)))

(defn port->endpoint [port]
  (str "tcp://localhost:" port))

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

(defrecord ZMQServer [port on-receive-fn]
  component/Lifecycle
  (start [this]
    (let [router (new-router-socket! port)]
      (assoc this :stop-channel (start-receiving! router on-receive-fn)
                  :router router)))
  (stop [this]
    (terminate-receiver-channel! (:stop-channel this))
    (terminate-router-socket! (:router this))
    (dissoc this :channels :router)))

(defn new-hive-server! [port on-receive-fn]
  (component/start (->ZMQServer port on-receive-fn)))

(defn terminate-hive-server! [server]
  (component/stop server))
