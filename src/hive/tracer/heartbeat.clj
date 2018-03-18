(ns hive.tracer.heartbeat
  (:require [clojure.core.async :as async :refer [<! go-loop timeout]]
            [hive.config :as config]
            [hive.storage.store :as store]
            [hive.tracer.adapters :as adapters])
  (:import java.time.LocalDateTime))

(def update-fn {:guchi        identity
                :unresponsive store/mark-as-unresponsive!
                :dead         store/mark-as-dead!})

(defn service-status [service]
  (let [{last-timestamp :last-timestamp} (second service)
        now                              (LocalDateTime/now)]
    (condp #(.isAfter %1 %2)  last-timestamp
      (.minusSeconds now config/death-threshold-s)         :dead
      (.minusSeconds now config/unresponsive-threshold-s) :unresponsive
      :guchi)))

(defn healthcheck-services! []
  (doseq [[status service] (group-by service-status (store/get-unresponsive-services))]
    (prn "changing service: " service " to status: " status)
    (and (get update-fn status) (adapters/service->service-name service))))

(defn start-heartbeat-checking! [seconds]
  (go-loop []
    (<! (timeout (* 1000 seconds)))
    (healthcheck-services!)
    (recur)))

(def terminate-heartbeat-checking! async/close!);;TODO- this does NOT close the channel :shrug:
