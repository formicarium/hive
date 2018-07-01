(ns hive.components.http
  (:require [clj-http.client :as client]))

(defn make-req
  [base-options base-url]
  (fn
    [{:keys [url method data extra-options]}]
    (let [full-url (str base-url url)
          options (merge base-options extra-options)]
      (case method
        :get (client/get full-url options)
        :post (client/post full-url (merge options {:form-params data}))))))