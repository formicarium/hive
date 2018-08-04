(ns hive.api.service
  (:require [com.walmartlabs.lacinia.pedestal :as lacinia]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [hive.api.graphql :as graphql]
            [io.pedestal.http.route.definition.table :as table]
            [hive.tanajura.client :as tanajura.client]
            [io.pedestal.http.body-params :as body-params]
            [hive.storage.api :as storage.api]
            [hive.storage.store :as store]))

(def common-interceptors [(body-params/body-params)])

(defn version [request]
  {:status 200
   :body   {:version 1}})

(defn service-deployed [store]
  (fn [{{:keys []} :json-params {:keys [name]} :path-params}]
    (storage.api/new-service name store)
    (tanajura.client/create-repo name)
    {:status 200
     :body   {:ok true}}))

(defn service-pushed [store]
  (fn [{{:keys [name]} :path-params}]
    (let [stinger (-> @(store/get-state store) :services (get name) :stinger-host)]
      ;; Ask for service git-api to pull from Tanajura
      )))

(defn rest-routes [store]
  [["/version" :get version :route-name :get-version]
   ["/services/:name/deployed" :post (conj common-interceptors (service-deployed store)) :route-name :service-deployed]
   ["/services/:name/pushed" :post (conj common-interceptors (service-pushed store)) :route-name :service-pushed]])

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
