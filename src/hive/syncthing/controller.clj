(ns hive.syncthing.controller
  (:require [hive.syncthing.syncthing-client :as syncthing.client]
            [hive.syncthing.logic :as syncthing.logic]))

(defn register-device [user-device-id {service-name :name {:keys [api-key device-id]} :syncthing}]
  (let [client (syncthing.client/new-syncthing-client (syncthing.logic/service->host service-name) api-key)]
    (syncthing.client/add-device client {:device-id user-device-id :name user-device-id})
    (syncthing.client/add-folder client {:path "app"} {:device-id user-device-id} {:device-id device-id})))
