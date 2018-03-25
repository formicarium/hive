(ns hive.core
  (:require [hive.tracer.server :as tracer.server]
            [hive.storage.store :as storage.store]
            [hive.api.server :as api.server]))

(def store (storage.store/new-store))

(defn start! []
  (tracer.server/new-hive-server! 9898 store)
  (api.server/run store))

(defn -main [& args]
  (start!))
