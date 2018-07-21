(ns hive.syncthing.logic
  (:require [schema.core :as s]
            [hive.syncthing.models :as syncthing.models]))

(s/defn new-device :- syncthing.models/Device
  [device-id :- s/Str, device-name :- s/Str]
  {})

(s/defn new-folder :- syncthing.models/Folder
  [path :- s/Str, ])
