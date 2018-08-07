(ns hive.api.resolvers.queries.events
  (:require [hive.storage.store :as store]))

(defn externalize [event]
  (-> (merge event (:meta event))
      (update :payload str)
      (assoc :receivedAt (-> event :meta :received-at str))
      (dissoc :received-at)))

(defn get-events [{{{:keys [storage]} :components} :request} _ _]
  (->> @(store/get-state storage)
       :events
       (map externalize)
       reverse))
