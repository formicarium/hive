(ns hive.syncthing.controller)


(defn register-device
  [device-id]
  (println (str "registering device id" device-id))
  true)

(defn get-syncthing
  []
  (println "get syncthing")
  {:deviceId "XABLAU"})


(defn register-folder
  [folder-id]
  (println (str "registering folder id" folder-id))
  true)