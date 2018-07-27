(ns hive.syncthing.logic
  (:require [hive.syncthing.models :as syncthing.models]
            [schema.core :as s]))

(def config-path "/config")
(def syncthing-api-port 2400)

(defn service->host [service-name] (str "http://" (name service-name) ":" syncthing-api-port))

(def device-req-defaults {:_addressesStr   "dynamic"
                          :compression     "metadata"
                          :introducer      false
                          :selectedFolders {}
                          :addresses       ["dynamic"]})

(s/defn new-device :- syncthing.models/DeviceRequest
  [device-id :- s/Str, device-name :- s/Str]
  (merge device-req-defaults {:name     device-name
                              :deviceID device-id}))

(defn- path->folder-id [path]
  path)

(def folder-req-defaults {:staggeredVersionsPath  ""
                          :type                   "readwrite"
                          :minDiskFree            {:value 1, :unit "%"}
                          :simpleKeep             5
                          :fsWatcherEnabled       true
                          :trashcanClean          0
                          :maxConflicts           10
                          :fsync                  true
                          :staggeredCleanInterval 3600
                          :fileVersioningSelector "none"
                          :order                  "random"
                          :autoNormalize          true
                          :rescanIntervalS        1
                          :staggeredMaxAge        365
                          :fsWatcherDelayS        10
                          :externalCommand        ""})

(s/defn new-folder :- syncthing.models/FolderRequest
  [path :- s/Str
   device]
  (let [abs-path  (clojure.string/join "/" [config-path path])
        folder-id (path->folder-id path)
        label     folder-id]
    (merge folder-req-defaults {:path    abs-path
                                :label   label
                                :id      folder-id
                                :devices [{:deviceID (:device-id device)}]})))



(defn with-device [folder {:keys [device-id]}]
  (update folder :devices conj {:deviceID device-id}))
