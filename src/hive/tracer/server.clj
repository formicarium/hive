(ns hive.tracer.server
  (:require [com.stuartsierra.component :as component]
            [hive.config :as config]
            [hive.tracer.handlers :as tracer.handlers]
            [hive.tracer.heartbeat :as tracer.heartbeat]
            [hive.tracer.zmq :as tracer.zmq]))

(defrecord ZMQServer [port on-receive-fn]
  component/Lifecycle
  (start [this]
    (let [router (tracer.zmq/new-router-socket! port)]
      (assoc this :stop-channel (tracer.zmq/start-receiving! router on-receive-fn)
             :heartbeat-channel (tracer.heartbeat/start-heartbeat-checking! config/healthcheck-timing-s)
             :router router)))
  (stop [this]
    (tracer.zmq/terminate-receiver-channel! (:stop-channel this))
    (tracer.heartbeat/terminate-heartbeat-checking! (:heartbeat-channel this))
    (tracer.zmq/terminate-router-socket! (:router this))
    (dissoc this :channels :router)))

(defn new-hive-server! [port]
  (component/start (->ZMQServer port tracer.handlers/dispatch-messages)))

(defn terminate-hive-server! [server]
  (component/stop server))
