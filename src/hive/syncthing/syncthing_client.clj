(ns hive.syncthing.syncthing-client
  (:require [clj-http.client :as http.client]
            [com.stuartsierra.component :as component]
            [hive.syncthing.logic :as logic]
            [hive.utils :as utils]))

(defprotocol Client
  (get-config [this])
  (set-config [this new-config])
  (add-device [this device])
  (add-folder [this folder device1 device2]))

(def base-opts {:content-type :json
                :accept       :json
                :as           :json})

(defn get-config! [{:keys [host api-key]}]
  (:body (http.client/request (merge base-opts
                                     {:url     (str host "/rest/system/config")
                                      :method  :get
                                      :headers {"X-API-Key" api-key}}))))

(defn set-config! [{:keys [host api-key]} payload]
  (:body (http.client/request (merge base-opts
                                     {:debug true
                                      :debug-body true
                                      :url         (str host "/rest/system/config")
                                      :method      :post
                                      :form-params payload
                                      :headers     {"X-API-Key" api-key}}))))

(defrecord SyncthingClient [host api-key]
  Client
  (get-config [this] (get-config! this))

  (set-config [this new-config] (set-config! this new-config))

  (add-device [this device]
    (set-config this (update (get-config this) :devices #(conj % (logic/new-device (:device-id device) (:name device))))))

  (add-folder [this folder device1 device2]
    (let [config (get-config this)
          folder-req (-> folder
                         :path
                         (logic/new-folder device1)
                         (logic/with-device device2))]
      (-> config
          (update :folders conj folder-req)
          (->> (set-config this))))))

(defn new-syncthing-client [host api-key]
  (->SyncthingClient host api-key))
