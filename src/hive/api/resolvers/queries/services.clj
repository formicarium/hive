(ns hive.api.resolvers.queries.services
  (:require [hive.storage.store :as store]
            [hive.utils :as utils]))

(defn get-services [{{{:keys [storage]} :components} :request} _ _]
  (-> @(store/get-state storage) utils/tap :services vals))
