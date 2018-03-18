(ns hive.storage.store
  (:require [clj-time.core :as t]))

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
                             (if (t/before? (:last-timestamp v) (t/ago (t/seconds 5)))
                               (conj acc (hash-map k v))
                               acc)) {} @registered-services*)]
        (prn "unresponsive-services: " res)
        res)))

(defn mark-as-unresponsive! [service-name]
  (when (@registered-services* service-name)
    (do (swap! registered-services* update-in [service-name] merge {:status :unresponsive})
        (prn "service is unresponsive: " service-name))))

(defn mark-as-dead! [service-name]
  (when (@registered-services* service-name)
    (do (swap! registered-services* update-in [service-name] merge {:status :dead})
        (prn "service is dead: " service-name))))
