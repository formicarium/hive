(ns hive.syncthing.controller
  (:require [hive.syncthing.logic :as logic]
            [hive.syncthing.syncthing-client :as syncthing.client]))

(def host "localhost:8384")
(def api-key "pimba123")
(def client (syncthing.client/new-syncthing-client host api-key))

(defn register-device [device-id name]
  (syncthing.client/add-device client {:device-id device-id :name name}))

(defn register-folder [folder-id]
  (syncthing.client/add-folder client ))
