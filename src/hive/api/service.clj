(ns hive.api.service
  (:require [com.walmartlabs.lacinia.pedestal :as lacinia]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [hive.api.graphql :as graphql]
            [io.pedestal.http.route.definition.table :as table]
            [io.pedestal.http.body-params :as body-params]
            [hive.storage.api :as storage.api]
            [hive.storage.store :as store]
            [hive.syncthing.logic :as syncthing.logic]
            [hive.syncthing.syncthing-client :as syncthing.client]))

(def common-interceptors [(body-params/body-params)])

(defn version [request]
  {:status 200
   :body   {:version 1}})

(defn service-deployed [store]
  (fn [{{:keys [api-key]} :json-params {:keys [name]} :path-params}]
    (let [client            (syncthing.client/new-syncthing-client (syncthing.logic/service->host name) api-key)
          service-device-id (:myID (syncthing.client/get-status client))]
      (storage.api/set-syncthing-config name service-device-id api-key store)
      {:status 200
       :body   @(store/get-state store)})))

(defn rest-routes [store]
  [["/version" :get version :route-name :get-version]
   ["/services/:name/deployed" :post (conj common-interceptors (service-deployed store)) :route-name :service-deployed]])

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
