(ns hive.api.resolvers.mutations.syncthing
  (:require [hive.storage.store :as store]
            [hive.syncthing.controller :as syncthing.controller]))

(defn register-device
  [{:keys [store]} {:keys [userDeviceId serviceName]} _]
  (let [service (-> @(store/get-state store) :services (get (keyword serviceName)))]
    (syncthing.controller/register-device userDeviceId service)))
