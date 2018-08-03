(ns hive.core
  (:gen-class)
  (:require [hive.tracer.server :as tracer.server]
            [hive.storage.store :as storage.store]
            [hive.repl :as repl]
            [hive.api.server :as api.server]))

(defonce store (storage.store/new-store))

(defn start! []
  (repl/new-repl-server! 2222)
  (tracer.server/new-hive-server! 9898 store)
  (api.server/run store))

(defn -main [& args]
  (start!))
