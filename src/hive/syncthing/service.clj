(ns hive.syncthing.service
  (:require [hive.syncthing.logic :as logic]
            [hive.components.http :as http]
            [hive.utils :as utils]
            [clojure.xml :as xml]
            [clj-http.client :as client])
  (:use [slingshot.slingshot :only [try+]]))

; Constants
(def st-config-path (str (System/getProperty "user.home") "/" ".syncthing/config.xml"))
(def st-host "http://localhost:8384")
(def bookmark
  {:config (str st-host "/rest/system/config")})

(defn get-api-key-from-parsed-xml
  "improve this"
  [parsed-xml]
  (get-in parsed-xml [:content 2 :content 1 :content 0]))

(defn get-st-config
  [path]
  (-> path
      xml/parse))

(def st-config (atom (get-st-config st-config-path)))

(defn read-st-config!
  [path]
  (->> path
       (get-st-config)
       (reset! st-config)))

(defn get-base-options [api-key] (utils/tap {:headers {"X-API-Key" api-key}
                                             :as      :json}))

(defn authd-req!
  [{:keys [url method options] :as params}]
  (try+
    (http/raw-req! {:url     url
              :method  method
              :options (merge (get-base-options (get-api-key-from-parsed-xml @st-config)) options)})
    (catch [:status 403] {}
      (print "error 403")
      (read-st-config! st-config-path)
      (authd-req! params))))

(defn post-config
  [config]
  (authd-req! {:method :post
              :url     (:config bookmark)
              :data    config}))

(defn get-config
  []
  (authd-req! {:method :get
              :url     (:config bookmark)}))

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