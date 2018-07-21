(ns hive.tracer.heartbeat
  (:require [clojure.core.async :as async]
            [hive.config :as config]
            [hive.storage.api :as storage.api]
            [hive.storage.store :as store])
  (:import java.time.LocalDateTime))

(defn service-status [{last-timestamp :last-timestamp}]
  (let [now (LocalDateTime/now)
        remove-time (.minusSeconds now config/remove-threshold-s)
        death-time (.minusSeconds now config/death-threshold-s)
        unresponsive-time (.minusSeconds now config/unresponsive-threshold-s)]
    (cond
      (nil? last-timestamp) nil
      (.isBefore last-timestamp remove-time) :removed
      (.isBefore last-timestamp death-time) :dead
      (.isBefore last-timestamp unresponsive-time) :unresponsive
      :else :healthy)))

(defn healthcheck-services! [store]
  (doseq [[service-name service-entry] (-> (store/get-state store) deref :services)]
    (when-let [service-status (service-status service-entry)]
      (prn "changing service: " service-name " to status: " service-status)
      (if (= service-status :removed)
        (storage.api/unregister-service service-name store) ;; Service has definitely died
        (storage.api/set-status service-status service-name store)))))

(defn start-heartbeat-checking! [seconds store]
  (let [stop-ch (async/chan)]
    (async/go-loop []
      (when (async/alt! stop-ch false (async/timeout (* 1000 seconds)) :keep-going)
        (healthcheck-services! store)
        (recur)))
    stop-ch))

(def terminate-heartbeat-checking! async/close!)
