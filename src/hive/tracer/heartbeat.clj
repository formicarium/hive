(ns hive.tracer.heartbeat
  (:require [chime :refer [chime-ch]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [hive.tracer.adapters :as adapters]
            [hive.config :as config]
            [hive.storage.store :as store]))

(defn heartbeat-ch [seconds]
  (chime-ch (rest (periodic-seq (t/now) (t/seconds seconds)))))

(def update-fn {:guchi        identity
                :unresponsive store/mark-as-unresponsive!
                :dead         store/mark-as-dead!})

(defn service-status [service]
  (let [{last-timestamp :last-timestamp} (second service)]
    (condp t/after? last-timestamp
      (t/ago (t/seconds config/death-threshold-s))        :dead
      (t/ago (t/seconds config/unresponsive-threshold-s)) :unresponsive
      :guchi)))

(defn healthcheck-services! []
  (doseq [[status service] (group-by service-status (store/get-unresponsive-services))]
    (prn "changing service: " service " to status: " status)
    (and (get update-fn status) (adapters/service->service-name service))))
