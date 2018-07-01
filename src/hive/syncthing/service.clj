(ns hive.syncthing.service
  (:require [hive.syncthing.logic :as logic]
            [hive.syncthing.request :as req]
            [hive.syncthing.constants :as constants]))

(defn post-config
  [config]
  (req/authd-req! {:method :post
              :url     (:config constants/bookmark)
              :data    config}))

(defn get-config
  []
  (req/authd-req! {:method :get
              :url     (:config constants/bookmark)}))

(defn update-config
  [update-fn & args]

  (let [current-config (:body (get-config))
        next-config (apply update-fn (concat (vector current-config) args))]
    (post-config next-config)))

(defn add-device
  [device]
  (update-config logic/add-device device))

(defn remove-device-by-name
  [device-name]
  (update-config logic/remove-device-by-name device-name))

(defn add-folder
  [folder]
  (update-config logic/add-folder folder))