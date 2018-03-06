(ns hive.tracer.server
    (:require [hive.tracer.zmq :as tracer.zmq]
              [hive.tracer.handlers :as tracer.handlers]))

(defrecord ZMQServer [port on-receive-fn]
  component/Lifecycle
  (start [this]
    (let [router (tracer.zmq/new-router-socket! port)]
      (assoc this :stop-channel (tracer.zmq/start-receiving! router on-receive-fn)
                  :router router)))
  (stop [this]
    (tracer.zmq/terminate-receiver-channel! (:stop-channel this))
    (tracer.zmq/terminate-router-socket! (:router this))
    (dissoc this :channels :router)))

(defn new-hive-server! [port]
  (component/start (->ZMQServer port tracer.handlers/dispatch-messages)))

(defn terminate-hive-server! [server]
  (component/stop server))
