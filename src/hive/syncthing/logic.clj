(ns hive.syncthing.logic
  (:require [hive.utils :as utils]
            [xml-in.core :as xml]))


(defn add-device
  [config device]
  (print device)
  (update config :devices #(conj % device)))


(defn remove-device-by-name
  [config name]
  (update config :devices (fn [devices]
                            (remove #(= (:name %) name) devices))))

(defn add-folder [config folder]
  (update config :folders #(conj % folder)))

(defn get-api-key-from-st-config
  "improve this"
  [st-config]
  (first (xml/find-first st-config [:configuration :gui :apikey])))

(def get-api-key-from-st-config-memo (memoize get-api-key-from-st-config))

(defn get-base-options [api-key] (utils/tap {:headers {"X-API-Key" api-key}
                                  :as                 :json
                                  :accept             :json
                                  :content-type       :json}))
