(ns hive.components.http
  (:require [clj-http.client :as client]))

(defn raw-req!
  [{:keys [url method options]}]
  (case method
    :get (client/get url options)
    :post (client/post url options)))