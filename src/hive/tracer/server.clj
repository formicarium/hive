(ns hive.tracer.server
  (:require [com.stuartsierra.component :as component]
            [hive.config :as config]
            [hive.tracer.handlers :as tracer.handlers]
            [hive.tracer.heartbeat :as tracer.heartbeat]
            [hive.tracer.zmq :as tracer.zmq]
            [clj-service.protocols.config :as protocols.config]))

(defrecord ZMQServer [on-receive-fn config storage]
  component/Lifecycle
  (start [this]
    (let [port   (protocols.config/get! config :tracer-port)
          router (tracer.zmq/new-router-socket! port)]
      (assoc this :stop-channel (tracer.zmq/start-receiving! router storage on-receive-fn)
                  :heartbeat-channel (tracer.heartbeat/start-heartbeat-checking! config/healthcheck-timing-s storage)
                  :router router)))
  (stop [this]
    (tracer.zmq/terminate-receiver-channel! (:stop-channel this))
    (tracer.heartbeat/terminate-heartbeat-checking! (:heartbeat-channel this))
    (tracer.zmq/terminate-router-socket! (:router this))
    (dissoc this :channels :router)))

(defn new-hive-server! []
  (map->ZMQServer {:on-receive-fn tracer.handlers/dispatch-messages}))
