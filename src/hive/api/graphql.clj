(ns hive.api.graphql
  (:require [com.walmartlabs.lacinia.schema :as schema]
            [hive.api.resolvers.queries.services :as queries.services]
            [hive.api.resolvers.queries.events :as queries.events]))

(def definitions
  {:enums
   {:EventType     {:description "types of events that hive can accept"
                    :values      [:newEvent :heartbeat :test]}
    :SpanDirection {:description "todo"
                    :values      [:consumer :producer]}
    :SpanType      {:description "todo"
                    :values      [:httpIn :httpOut :kafka]}
    :SpanKind      {:description "todo"
                    :values      [:start :end]}
    :ServiceStatus {:description "Health Status of a Service"
                    :values      [:healthy :unresponsive :dead]}}

   :objects
   {:HttpTags    {:description "Basic info about http calls"
                  :fields      {:method     {:type '(non-null String)}
                                :statusCode {:type 'Int}
                                :url        {:type '(non-null String)}}}
    :Peer        {:description "info about the remote"
                  :fields      {:port    {:type '(non-null Int)}
                                :service {:type 'String}}}
    :SpanTags    {:description "All the needed info for debugging/tracing"
                  :fields      {:http      {:type '(non-null :HttpTags)}
                                :peer      {:type '(non-null :Peer)}
                                :direction {:type '(non-null :SpanDirection)}
                                :type      {:type '(non-null :SpanType)}
                                :kind      {:type '(non-null :SpanKind)}}}
    :SpanContext {:description "tracing definition data"
                  :fields      {:traceId  {:type '(non-null String)}
                                :parentId {:type '(non-null String)}
                                :spanId   {:type '(non-null String)}}}
    :Meta        {:description "Metadata about event"
                  :fields      {:type      {:type '(non-null :EventType)}
                                :timestamp {:type 'String}
                                :service   {:type '(non-null String)}}}
    :Span        {:description "A tracing/debugging span"
                  :fields      {:timestamp {:type 'String}
                                :tags      {:type '(non-null :SpanTags)}
                                :payload   {:type 'String}
                                :context   {:type '(non-null :SpanContext)}}}
    :Event       {:description "Represents an IO Event sent by a service to Hive"
                  :fields      {:identity {:type '(non-null String)}
                                :meta     {:type :Meta}
                                :payload  {:type :Span}}}
    :Service     {:description "Represents a connected service to Hive"
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
