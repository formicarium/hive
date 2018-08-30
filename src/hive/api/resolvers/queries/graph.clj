(ns hive.api.resolvers.queries.graph
  (:require [hive.storage.store :as store]
            [schema.core :as s])
  (:import java.util.UUID
           java.time.LocalDateTime))

(defn externalize [event]
  (-> (merge event (:meta event))
      (update :payload str)
      (assoc :receivedAt (-> event :meta :received-at str))
      (dissoc :received-at)))

(def m {:http {:source :service
               :target :service}
        :kafka {:source :service
                :target :topic}})

(defn- payload-type->source-type [payload-type]
  (:source (get m payload-type)))

(defn- payload-type->target-type [payload-type]
  (:target (get m payload-type)))

(defn kafka-event->node [event]
  (if (= :producer (:direction (:payload event)))
    {:id (:topic (:endpoint (:data (:payload event))))
     :name (:topic (:endpoint (:data (:payload event))))
     :type :topic}
    {:id (:identity event)
     :name (:identity event)
     :type :service}))

(defn http-event->node [event]
  {:id (:identity event)
   :name (:identity event)
   :type :service})

(defn event->node [v]
  (if (= :kafka (:type (:payload v)))
    (kafka-event->node v)
    (http-event->node v)))

(defn events->nodes [events]
  (reduce (fn [acc v]
            (conj acc (event->node v))) #{}  events))


(defn- kafka-event->edge-source [v]
  (if (= :producer (:direction (:payload v)))
         {:id (:identity v)}
         {:id (:topic (:endpoint (:data (:payload v))))}))

(defn- http-event->edge-source [v]
  {:id (:identity v)})

(defn- event->edge-source [event]
  (if (= :kafka (:type (:payload event)))
    (kafka-event->edge-source event)
    (http-event->edge-source event)))

(defn- kafka-event->edge-target [v]
  (if (= :producer (:direction (:payload v)))
    {:id (:topic (:endpoint (:data (:payload v))))}
    {:id (:identity v)}))

(defn- http-event->edge-target [v]
  {:id (name (:service (:endpoint (:data (:payload v)))))})

(defn- event->edge-target [event]
  (if (= :kafka (:type (:payload event)))
    (kafka-event->edge-target event)
    (http-event->edge-target event)))

(defn event->type [event]
  (:type (:payload event)))

(defn events->edges [events]
  (reduce (fn [acc v]
            (conj acc {:source (event->edge-source v)
                       :target (event->edge-target v)
                       :type (event->type v)
                       :event (assoc v :type (:type (:payload v)))})) [] events))

(defn build-graph [events]
  {:nodes (events->nodes events)
   :edges (events->edges events)})

(def chumbado [{:identity "tyrion"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 0)
                           :service   :tyrion}
                :payload  {:type      :http
                           :direction :out-request
                           :data {:endpoint {:uri "/api/collections"
                                             :service :sr-barriga}}}}
               {:identity "sr-barriga"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 1)
                           :service   :sr-barriga}
                :payload {:type :http
                          :direction :in-request
                          :data {:endpoint {:uri "/api/collections"
                                            :service :tyrion}}}}
               {:identity "sr-barriga"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 2)
                           :service   :sr-barriga}
                :payload {:type :http
                          :direction :in-response
                          :data {:endpoint {:uri "/api/collections"
                                            :service :tyrion}}}}
               {:identity "tyrion"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 3)
                           :service   :tyrion}
                :payload  {:type      :http
                           :direction :out-response
                           :data {:endpoint {:uri "/api/collections"
                                             :service :sr-barriga}}}}
               {:identity "tyrion"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 4)
                           :service   :tyrion}
                :payload  {:type      :kafka
                           :direction :producer
                           :data {:endpoint {:topic "debt-updated"}}}}
               {:identity "line-items"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 5)
                           :service   :line-items}
                :payload  {:type      :kafka
                           :direction :consumer
                           :data {:endpoint {:topic "debt-updated"}}}}])

(defn get-graph [{store :store} arguments value]
  (->> chumbado
       (build-graph)))
