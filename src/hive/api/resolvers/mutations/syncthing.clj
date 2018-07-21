(ns hive.api.resolvers.mutations.syncthing
  (:require [hive.syncthing.controller :as syncthing.controller]))

(defn register-device
  [_ {:keys [deviceId name]} _]
  (syncthing.controller/register-device deviceId name))

(defn register-folder
  [_ {:keys [folderId]} _]
  (syncthing.controller/register-folder folderId))
