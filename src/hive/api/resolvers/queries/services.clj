(ns hive.api.resolvers.queries.services
  (:require [hive.storage.store :as store]
            [hive.utils :as utils]))

(defn get-services [{store :store} _ _]
  (-> @(store/get-state store) utils/tap :services vals))
