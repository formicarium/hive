(ns hive.api.resolvers.mutations.syncthing
  (:require [hive.storage.store :as store]
            [hive.utils :as utils]
            [hive.syncthing.controller :as syncthing.controller]))

(defn register-device
  [{:keys [store]} {:keys [userDeviceId serviceName]} _]
  (let [services (utils/tap (-> @(store/get-state store) :services))
        service (utils/tap ((keyword serviceName) services))]
    (syncthing.controller/register-device userDeviceId service)))
