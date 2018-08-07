(ns hive.repl
  (:require [clojure.tools.nrepl.server :as nrepl.server]
            [com.stuartsierra.component :as component]
            [clj-service.protocols.config :as protocols.config]))

(defrecord ReplServer [config]
  component/Lifecycle
  (start [this]
    (->> (protocols.config/get! config :repl-port)
         (nrepl.server/start-server :port)
         (assoc this :server)))
  (stop [this]
    (nrepl.server/stop-server (:server this))
    (dissoc this :server)))

(defn new-repl-server! []
  (map->ReplServer {}))
