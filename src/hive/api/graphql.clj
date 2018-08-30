(ns hive.api.graphql
  (:require [com.walmartlabs.lacinia.schema :as schema]
            [hive.api.resolvers.queries.services :as queries.services]
            [hive.api.resolvers.queries.events :as queries.events]))

(def definitions
  {:enums
   {:ServiceStatus {:description "Health Status of a Service"
                    :values      [:healthy :unresponsive :dead]}
    :NodeType      {:description "Type of a Node"
                    :values      [:consumer :service]}}

   :objects
   {:Node      {:description "Node of the graph"
                :fields      {:id   {:type '(non-null String)}
                              :name {:type '(non-null String)}
                              :type {:type '(non-null :NodeType)}}}

    :Edge      {:description "Edge of the graph"
                :fields      {:source {:type '(non-null :Node)}
                              :target {:type '(non-null :Node)}
                              :event  {:type '(non-null :Event)}}}

    :Graph     {:description "Semi-materialized graph"
                :fields      {:nodes {:type '(list :Node)}
                              :edges {:type '(list :Edge)}}}

    :Event   {:description "Represents an IO Event sent by a service to Hive"
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
