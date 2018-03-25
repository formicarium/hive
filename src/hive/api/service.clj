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

(defn app-routes [store]
  (table/table-routes
    (concat (lacinia/graphql-routes graphql/Schema {:graphiql    true
                                                    :app-context {:store store}})
            rest-routes)))

(defn service [store]
  (lacinia/service-map graphql/Schema {:graphiql      true
                                       :subscriptions true
                                       :routes        (app-routes store)
                                       :app-context   {:store store}}))
