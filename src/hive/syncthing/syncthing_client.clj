(ns hive.syncthing.syncthing-client
  (:require [com.stuartsierra.component :as component]
            [clj-http.client :as http.client]))

(defprotocol Client
  (get-config [this])
  (set-config [this new-config])
  (add-device [this device])
  (add-folder [this folder]))

(def base-opts {:content-type :json
                :accept       :json
                :as           :json})

(defn get-config! [[{:keys [host api-key]}]]
  (http.client/request (merge base-opts
                              {:url     (str host "/rest/system/config")
                               :method  :get
                               :headers {"X-API-Key" api-key}})))

(defn set-config! [{:keys [host api-key]} payload]
  (http.client/request (merge base-opts
                              {:url         (str host "/rest/system/config")
                               :method      :post
                               :form-params payload
                               :headers     {"X-API-Key" api-key}})))

(defrecord SyncthingClient [host api-key]
  component/Lifecycle
  (start [this]
    )
  (stop [this]
    )

  Client
  (get-config [this] (get-config! this))

  (set-config [this new-config] (set-config! this new-config))

  (add-device [this device]
    )

  (add-folder [this folder]
    ))
