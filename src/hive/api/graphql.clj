(ns hive.api.graphql
  (:require [com.walmartlabs.lacinia.schema :as schema]
            [hive.api.resolvers.mutations.syncthing :as mutations.syncthing]
            [hive.api.resolvers.queries.services :as queries.services]
            [hive.api.resolvers.queries.syncthing :as queries.syncthing]
            [hive.api.resolvers.queries.events :as queries.events]))

(def definitions
  {:enums
   {:ServiceStatus {:description "Health Status of a Service"
                    :values      [:healthy :unresponsive :dead]}}

   :objects
   {:Event     {:description "Represents an IO Event sent by a service to Hive"
                :fields      {:payload    {:type 'String}
                              :producedAt {:type 'String}
                              :receivedAt {:type '(non-null String)}
                              :service    {:type '(non-null String)}}}

    :Service   {:description "Represents a connected service to Hive"
                :fields      {:name    {:type '(non-null String)}
                              :version {:type 'String}
                              :status  {:type '(non-null :ServiceStatus)}}}
    :Syncthing {:description "Represents the syncthing instance running with Hive"
                :fields      {:deviceId {:type '(non-null String)}}}
    }})

(def queries
  {:queries
   {:syncthing {:type    :Syncthing
                :resolve queries.syncthing/get-syncthing}
    :services  {:type    '(list :Service)
                :resolve queries.services/get-services}
    :events    {:type    '(list :Event)
                :resolve queries.events/get-events}}})

(def mutations
  {:mutations
   {:registerDevice {:type    '(non-null Boolean)
                     :args    {:deviceId {:type '(non-null String)}
                               :name     {:type '(non-null String)}}
                     :resolve mutations.syncthing/register-device}
    :registerFolder {:type    '(non-null Boolean)
                     :args    {:folderId {:type '(non-null String)}}
                     :resolve mutations.syncthing/register-folder}}})

(def subscriptions
  {:subscriptions {}})

(def Schema (schema/compile (merge definitions queries mutations subscriptions)))
