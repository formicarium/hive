(ns hive.core
  (:require [hive.tracer.server :as tracer.server]
            [hive.storage.store :as storage.store]
            [hive.api.server :as api.server]))

(defn start! []
  (let [store (storage.store/new-store)]
    (tracer.server/new-hive-server! 9898 store)
    (api.server/run)))

(defn -main [& args]
  (start!))
