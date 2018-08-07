(ns hive.api.routes
  (:require [com.walmartlabs.lacinia.pedestal :as lacinia]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [clj-service.pedestal.interceptors.error :as int-err]
            [hive.api.graphql :as graphql]
            [com.walmartlabs.lacinia.pedestal :as lacinia.pedestal]
            [io.pedestal.http.route.definition.table :as table]
            [clj-service.pedestal.interceptors.adapt :as int-adapt]
            [clj-service.pedestal.interceptors.schema :as int-schema]
            [io.pedestal.http.body-params :as body-params]))

(def common-interceptors [int-err/catch!
                          (body-params/body-params)
                          int-adapt/coerce-body
                          int-adapt/content-neg-intc
                          int-schema/coerce-output])

(defn version [_]
  {:status 200
   :body   {:version 1}})

(def rest-routes
  [["/version" :get (conj common-interceptors version) :route-name :get-version]])

(def routes
  (table/table-routes
    (concat (lacinia/graphql-routes graphql/Schema
              {:graphiql     true
               :interceptors (-> (lacinia.pedestal/default-interceptors graphql/Schema {:graphiql true})
                                 (lacinia.pedestal/inject int-err/catch! :before ::lacinia.pedestal/json-response))})
            rest-routes)))
