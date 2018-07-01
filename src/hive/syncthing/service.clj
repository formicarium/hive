(ns hive.syncthing.service
  (:require [hive.syncthing.logic :as logic]
            [hive.components.http :as http]))

(def bookmark
  {:config "/rest/system/config"})

(def api-key "W3dbDV4atwUxzWpzU4GRvXUUSxkbucQA")
(def st-host "http://localhost:8384")

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
  (clojure.pprint/pprint config)
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