(ns hive.components
  (:require [com.stuartsierra.component :as component]
            [clj-service.components.config :as components.config]
            [clj-service.components.webapp :as components.webapp]
            [clj-service.components.pedestal :as components.pedestal]
            [hive.api.routes :as api.routes]
            [hive.tracer.server :as tracer.server]
            [hive.storage.store :as storage.store]
            [hive.repl :as repl]))

(defonce system (atom nil))

(def base-system
  (component/system-map
    :config (components.config/new-config "config.edn")
    :storage (storage.store/new-store)
    :webapp (component/using (components.webapp/new-webapp) [:storage :config])
    :pedestal (component/using (components.pedestal/new-pedestal #'api.routes/routes) [:config :webapp])
    :trace-server (component/using (tracer.server/new-hive-server!) [:config :storage])
    :repl-server (component/using (repl/new-repl-server!) [:config])))

(defn start-system! []
  (reset! system (component/start-system base-system)))
