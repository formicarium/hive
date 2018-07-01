(ns hive.syncthing.controller
  (:require [hive.syncthing.service :as syncthing.service]))


(defn register-device
  [device-id name]
  (println (str "registering device id " device-id))
  (syncthing.service/add-device {:deviceID device-id
                                 :addresses ["dynamic"]
                                 :introducer false
                                 :name name})
  true)

(defn get-syncthing
  []
  (println "get syncthing")
  {:deviceId (syncthing.service/get-my-id)})


(defn register-folder
  [folder-id]
  (println (str "registering folder id " folder-id))
  (syncthing.service/add-folder {:id      folder-id
                                 :path    "/tmp/teste"})
  true)
