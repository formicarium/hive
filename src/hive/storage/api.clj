(ns hive.storage.api
  (:require [hive.storage.store :as store])
  (:import java.time.LocalDateTime))

(defn register-new-service [service-name store]
  (swap! (store/get-state store) update-in [:services service-name] #(merge % {:name           service-name
                                                                               :status         :healthy
                                                                               :last-timestamp (LocalDateTime/now)})))

(defn unregister-service [service-name store]
  (swap! (store/get-state store) update :services #(dissoc % service-name)))

(defn touch-service [service-name version store]
  (swap! (store/get-state store) update-in [:services service-name] #(merge % {:name           service-name
                                                                               :status         :healthy
                                                                               :version        version
                                                                               :last-timestamp (LocalDateTime/now)})))

(defn add-new-event [event store]
  (prn "NEW_EVENT" event)
  (swap! (store/get-state store) update :events #(conj % event)))

(defn set-status [status service-name store]
  (swap! (store/get-state store) update-in [:services service-name] assoc :status status))

(defn new-service [service-name store]
  (swap! (store/get-state store) update-in [:services service-name] #(merge % {:name   service-name
                                                                               :status :waiting})))
