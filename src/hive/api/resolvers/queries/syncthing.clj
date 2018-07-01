(ns hive.api.resolvers.queries.syncthing
  (:require [hive.syncthing.controller :as syncthing.controller]))


(defn get-syncthing
  [_ _ _]
  (syncthing.controller/get-syncthing))