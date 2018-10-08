(ns hive.api.resolvers.queries.events
  (:require [hive.storage.store :as store]
            [hive.tracer.adapters :as adapters]))

(defn get-events [{{{:keys [storage]} :components} :request} _ _]
  (->> @(store/get-state storage)
       :events
       (map adapters/externalize)
       reverse))
