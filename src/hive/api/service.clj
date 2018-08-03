(ns hive.api.service
  (:require [com.walmartlabs.lacinia.pedestal :as lacinia]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [hive.api.graphql :as graphql]
            [io.pedestal.http.route.definition.table :as table]
            [io.pedestal.http.body-params :as body-params]
            [hive.storage.api :as storage.api]
            [hive.storage.store :as store]))

(def common-interceptors [(body-params/body-params)])

(defn version [request]
  {:status 200
   :body   {:version 1}})

(defn service-deployed [store]
  (fn [{{:keys [api-key device-id]} :json-params {:keys [name]} :path-params}]
    (storage.api/new-service name device-id api-key store)
    {:status 200
     :body   @(store/get-state store)}))

(defn service-pushed [store]
  (fn [{{:keys [name]} :path-params}]
    (let [git-api (-> @(store/get-state store) :services (get name) :git-api)]
      ;; Ask for service git-api to pull from Tanajura
      )))

(defn rest-routes [store]
  [["/version" :get version :route-name :get-version]
   ["/services/:name/deployed" :post (conj common-interceptors (service-deployed store)) :route-name :service-deployed]
   ["/service/:name/pushed" :post (conj common-interceptors (service-pushed store)) :route-name :service-pushed]])

(defn app-routes [store]
  (table/table-routes
    (concat (lacinia/graphql-routes graphql/Schema {:graphiql    true
                                                    :app-context {:store store}})
            (rest-routes store))))

(defn service [store]
  (lacinia/service-map graphql/Schema {:graphiql      true
                                       :subscriptions true
                                       :routes        (app-routes store)
                                       :app-context   {:store store}}))
