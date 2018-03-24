(ns hive.api.service
  (:require [com.walmartlabs.lacinia.pedestal :as lacinia]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [hive.api.graphql :as graphql]
            [io.pedestal.http.route.definition.table :as table]))

(defn version [request]
  {:status 200
   :body   {:version 1}})

(def rest-routes
  [["/version" :get version :route-name :get-version]])

(def app-routes
  (table/table-routes
    (concat (lacinia/graphql-routes graphql/Schema {:graphiql true})
            rest-routes)))

(def service (lacinia/service-map graphql/Schema {:graphiql      true
                                                  :subscriptions true
                                                  :routes        app-routes}))
