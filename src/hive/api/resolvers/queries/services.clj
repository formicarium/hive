(ns hive.api.resolvers.queries.services
  (:require [hive.storage.store :as store]))

(defn get-services [{store :store} arguments value]
  (-> @(store/get-state store) :services vals))
