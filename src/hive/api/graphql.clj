(ns hive.api.graphql
  (:require [com.walmartlabs.lacinia.schema :as schema]
            [hive.api.resolvers.queries.services :as queries.services]))

(def definitions
  {:enums
   {:ServiceStatus {:description "Health Status of a Service"
                    :values      [:healthy :unresponsive :dead]}}

   :objects
   {:Service {:description "Represents a connected service to Hive"
              :fields      {:name   {:type '(non-null String)}
                            :status {:type '(non-null :ServiceStatus)}}}
    :Event   {:description "Represents an IO Event sent by a service to Hive"
              :fields      {:payload {:type 'String}}}}})

(def queries
  {:queries
   {:services {:type    '(list :Service)
               :resolve queries.services/get-services}}})

(def mutations
  {:mutations {}})

(def subscriptions
  {:subscriptions {}})

(def Schema (schema/compile (merge definitions queries mutations subscriptions)))
