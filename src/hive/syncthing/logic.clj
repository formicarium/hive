(ns hive.syncthing.logic
  (:require [hive.storage.models :as storage.models]
            [hive.syncthing.models :as syncthing.models]
            [schema.core :as s]
            [hive.storage.models :as storage.models]))

(def config-path "/config")

(def device-req-defaults {:name "syncthing2",
                          :deviceID
                          "WO4ZVCR-6KHMJLX-FNC3NZA-XHGR5CY-4KVLPVY-NBWYNPK-EVTW3CS-75NQ4AB",
                          :_addressesStr "dynamic",
                          :compression "metadata",
                          :introducer false,
                          :selectedFolders {},
                          :addresses ["dynamic"]})

(s/defn new-device :- syncthing.models/DeviceRequest
  [device-id :- s/Str, device-name :- s/Str]
  (merge device-req-defaults {:name device-name
                              :deviceID device-id}))

(defn- path->folder-id [path]
  path)

(def folder-req-defaults {:path "/config/testando",
                          :staggeredVersionsPath "",
                          :type "readwrite",
                          :minDiskFree {:value 1, :unit "%"},
                          :simpleKeep 5,
                          :fsWatcherEnabled true,
                          :trashcanClean 0,
                          :maxConflicts 10,
                          :fsync true,
                          :staggeredCleanInterval 3600,
                          :label "testando",
                          :id "4apjv-5jjry",
                          :fileVersioningSelector "none",
                          :devices
                          [{:deviceID
                            "MQDJBTV-ENVZY34-IQF53LC-XVD5XS4-HNHW766-VEN5ASZ-I5CZA5F-W3TPDAE"}],
                          :order "random",
                          :autoNormalize true,
                          :rescanIntervalS 3600,
                          :staggeredMaxAge 365,
                          :fsWatcherDelayS 10,
                          :externalCommand ""})

(s/defn new-folder :- syncthing.models/FolderRequest
  [path :- s/Str
   device :- syncthing.models/MinimalDevice]
  (let [abs-path (clojure.string/join "/" [config-path path])
        folder-id (path->folder-id path)
        label folder-id]
    (merge folder-req-defaults {:path abs-path
                                :label label
                                :id folder-id
                                :devices [device]})))

(defn with-device [folder device]
  (update folder :devices #(conj device)))
