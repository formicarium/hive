(ns hive.api.graphql
  (:require [com.walmartlabs.lacinia.schema :as schema]
            [hive.api.resolvers.queries.services :as queries.services]
            [hive.api.resolvers.mutations.services :as mutations.services]
            [hive.api.resolvers.queries.events :as queries.events]))

(def definitions
  {:enums
   {:ServiceStatus {:description "Health Status of a Service"
                    :values      [:healthy :unresponsive :dead]}}

   :objects
   {:Event   {:description "Represents an IO Event sent by a service to Hive"
              :fields      {:payload    {:type 'String}
                            :producedAt {:type 'String}
                            :receivedAt {:type '(non-null String)}
                            :service    {:type '(non-null String)}}}

    :Service {:description "Represents a connected service to Hive"
              :fields      {:name    {:type '(non-null String)}
                            :version {:type 'String}
                            :status  {:type '(non-null :ServiceStatus)}}}}})

(def queries
  {:queries
   {:services {:type    '(list :Service)
               :resolve queries.services/get-services}
    :events   {:type    '(list :Event)
               :resolve queries.events/get-events}}})

(def mutations
  {:mutations {}})

(def subscriptions
  {:subscriptions {}})

(def Schema (schema/compile (merge definitions queries mutations subscriptions)))
