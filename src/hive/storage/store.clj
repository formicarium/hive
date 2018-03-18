(ns hive.storage.store
  (:require [clj-time.core :as t]
            [hive.config :as config]))

(def registered-services* (atom {:pimba {:status :ok :last-timestamp (t/now)}}))
(def received-events* (atom []))

(defn register-new-service [service-name]
  (swap! registered-services* conj service-name {:last-timestamp 0}))

(defn unregister-service [service-name]
  (swap! registered-services* dissoc service-name))

(defn touch-service [service-name]
  (swap! registered-services* update-in [service-name] merge {:last-timestamp (t/now)}))

(defn add-new-event [event]
  (swap! received-events* conj event))

(defn get-unresponsive-services []
  (do (let [res (reduce-kv (fn [acc k v]
                             (if (t/before? (:last-timestamp v)
                                            (t/ago (t/seconds config/unresponsive-threshold-s)))
                               (conj acc (hash-map k v))
                               acc)) {} @registered-services*)]
        (prn "unresponsive-services: " res)
        res)))

(defn with-status [status service service-name]
  (update-in service [service-name] merge {:status status}))

(def with-dead-status (partial with-status :dead))
(def with-unresponsive-status (partial with-status :unresponsive))

(defn mark-as-unresponsive! [service-name]
  (when (@registered-services* service-name)
    (do (swap! registered-services* with-unresponsive-status service-name)
        (prn "service is unresponsive: " service-name))))

(defn mark-as-dead! [service-name]
  (when (@registered-services* service-name)
    (do (swap! registered-services* with-dead-status service-name)
        (prn "service is dead: " service-name))))
