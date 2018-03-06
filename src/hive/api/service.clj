(ns hive.api.service
    (:require [io.pedestal.http :as http]
              [io.pedestal.http.route :as route]
              [io.pedestal.http.body-params :as body-params]
              [io.pedestal.http.route.definition :refer [defroutes]
              [hive.storage.store :as store]]))
  
(defn hello-world [request]
    (let [name (get-in request [:params :name] "World")]
      {:status 200 :body (str "Hello " name "!\n")}))
  
(defn get-events [request]
    {:status 200
     :body {:events @store/received-events*}})

(defroutes routes
    [[["/"
        ["/hello" {:get hello-world}
        ["/events" {:get get-events}]]]]])
  
(def service {:env                 :prod
              ::http/routes        routes
              ::http/resource-path "/public"
              ::http/type          :jetty
              ::http/port          8080})