(ns hive.syncthing.controller
  (:require [hive.syncthing.logic :as logic]
            [hive.syncthing.syncthing-client :as syncthing.client]))

(def host "http://localhost:8384")
(def api-key "pimba123")
(def client (syncthing.client/new-syncthing-client host api-key))

(defn register-device [device-id name]
  (syncthing.client/add-device client {:device-id device-id :name name}))

(defn register-folder [folder-id]
  #_(syncthing.client/add-folder client folder device));Wip- get folder req data from lacinia
