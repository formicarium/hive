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
    {:id (:endpoint (:data (:payload event)))
     :name (:endpoint (:data (:payload event)))
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

(comment (event->node {:identity "line-items"
                       :meta     {:type      :new-event
                                  :timestamp (LocalDateTime/of 2018 01 01 12 0 5)
                                  :service   :line-items}
                       :payload  {:type      :kafka
                                  :direction :consumer
                                  :data {:falou :mana}}}))

(defn events->nodes [events]
  (reduce (fn [acc v]
            (conj acc (event->node v))) #{}  events))

(defn event->edge [{{payload-type :type} :payload identity :identity :as event}]
  (let [source {:id (str identity "-" (UUID/randomUUID))
                :name (clojure.string/join "-" [identity payload-type])
                :type (payload-type->source-type payload-type)}
        target {:id (str identity "-" (UUID/randomUUID))
                :name (clojure.string/join "-" [identity payload-type])
                :type (payload-type->target-type payload-type)}]
    {:source source
     :target target
     :event event}))

(defn build-graph [events]
  (->> events
       (map event->edge)))

(defn get-graph [{store :store} arguments value]
  (->> @(store/get-state store)
       :events
       (build-graph)))

#_(comment
  (def events [{:identity "tyrion"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 0)
                           :service   :tyrion}
                :payload  {:type      :http
                           :direction :out-request
                           :data {:eae :men}}}
               {:identity "sr-barriga-supimpa"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 1)
                           :service   :sr-barriga}
                :payload {:type :http
                          :direction :in-request
                          :data {:eae :men}}}
               {:identity "sr-barriga"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 2)
                           :service   :sr-barriga}
                :payload {:type :http
                          :direction :in-response
                          :data {:eae :men}}}
               {:identity "tyrion"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 3)
                           :service   :tyrion}
                :payload  {:type      :http
                           :direction :out-response
                           :data {:eae :men}}}
               {:identity "tyrion"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 4)
                           :service   :tyrion}
                :payload  {:type      :kafka
                           :direction :producer
                           :data {:eae :men}}}
               {:identity "line-items"
                :meta     {:type      :new-event
                           :timestamp (LocalDateTime/of 2018 01 01 12 0 5)
                           :service   :line-items}
                :payload  {:type      :kafka
                           :direction :consumer
                           :data {:falou :mana}}}])
  (events->edges events )
  (def expected-nodes [{:id   "tyrion"
                        :name "tyrion"
                        :type :service}
                       {:id   "sr-barriga"
                        :name "sr-barriga"
                        :type :service}
                       {:id   "debt-updated"
                        :name "debt-updated"
                        :type :topic}])
  {:source {:id   "tyrion"}
   :target {:id   "sr-barriga"}
   :event  {:identity "tyrion"
            :meta     {:type      :new-event
                       :timestamp (LocalDateTime/of 2018 01 01 12 0 0)
                       :service   :tyrion}
            :payload  {:type      :http
                       :direction :out-request
                       :data {:eae :men}}}}

  {:source {:id   "sr-barriga"}
   :target {:id   "tyrion"}
   :event  {:identity "sr-barriga"
            :meta     {:type      :new-event
                       :timestamp (LocalDateTime/of 2018 01 01 12 0 1)
                       :service   :sr-barriga}
            :payload {:type :http
                      :direction :in-request
                      :data {:eae :men}}}}

  {:source {:id   "sr-barriga"}
   :target {:id   "tyrion"}
   :event  {:identity "sr-barriga"
            :meta     {:type      :new-event
                       :timestamp (LocalDateTime/of 2018 01 01 12 0 2)
                       :service   :sr-barriga}
            :payload {:type :http
                      :direction :in-response
                      :data {:eae :men}}}}

  {:source {:id   "tyrion"}
   :target {:id   "sr-barriga"}
   :event  {:identity "tyrion"
            :meta     {:type      :new-event
                       :timestamp (LocalDateTime/of 2018 01 01 12 0 3)
                       :service   :tyrion}
            :payload  {:type      :http
                       :direction :out-response
                       :data {:eae :men}}}}

  {:source {:id   "tyrion"}
   :target {:id   "debt-updated"}
   :event  {:identity "tyrion"
            :meta     {:type      :new-event
                       :timestamp (LocalDateTime/of 2018 01 01 12 0 4)
                       :service   :tyrion}
            :payload  {:type      :kafka
                       :direction :producer
                       :data {:eae :men}}}}

  {:source {:id   "debt-updated"}
   :target {:id   "line-items"}
   :event  {:identity "line-items"
            :meta     {:type      :new-event
                       :timestamp (LocalDateTime/of 2018 01 01 12 0 5)
                       :service   :line-items}
            :payload  {:type      :kafka
                       :direction :consumer
                       :data {:falou :mana}}}}

                                        ;tyrion -> debt-updated
                                        ;debt-updated -> sr-barriga
                                        ;debt-updated -> line-items
                                        ;tyrion -> sr-barriga (out-request)
                                        ;sr-barriga (in-request) from tyrion
                                        ;sr-barriga (out-response) to tyrion
                                        ;tyrion -. sr-barriga (out-response)
  )
