(ns hive.core
  (:require [hive.tracer.server :as tracer.server]
            [hive.api.server :as api.server]))

(defn -main [& args]
  (tracer.server/new-hive-server! 9898)
  (api.server/run))
