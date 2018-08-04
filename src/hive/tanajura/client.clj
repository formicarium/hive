(ns hive.tanajura.client
  (:require [clj-http.client :as http.client]))

(def tanajura-host "http://tanajura")

(defn create-repo [service-name]
  (http.client/post (str tanajura-host "/api/repo")
    {:form-params  {:name service-name}
     :as           :json
     :content-type :json
     :accept       :json}))
