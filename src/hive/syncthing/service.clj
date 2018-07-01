(ns hive.syncthing.service
  (:require [hive.syncthing.logic :as logic]
            [hive.components.http :as http]
            [clojure.xml :as xml]))

(def bookmark
  {:config "/rest/system/config"})
(def st-host "http://localhost:8384")
(def st-config-path (str (System/getProperty "user.home") "/" ".syncthing/config.xml"))

(defn get-api-key-from-parsed-xml
  "improve this"
  [parsed-xml]
  (get-in parsed-xml [:content 2 :content 1 :content 0]))

(defn get-st-config
  [path]
  (-> path
      xml/parse))

(def st-config (get-st-config st-config-path))
(def api-key (get-api-key-from-parsed-xml st-config))


(def st-base-options {:headers      {"X-API-Key" api-key}
                      :content-type :json
                      :accept       :json
                      :as           :json})


(def st-req (http/make-req st-base-options st-host))

(defn get-config
  []
  (st-req {:method :get
           :url    (:config bookmark)}))

(defn post-config
  [config]
  (st-req {:method :post
           :url    (:config bookmark)
           :data   config}))


(defn print-ret [x]
  (clojure.pprint/pprint x)
  x)

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