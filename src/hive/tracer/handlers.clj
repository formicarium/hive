(ns hive.tracer.handlers
  (:require [hive.storage.api :as storage.api])
  (:import [java.time LocalDateTime]))

(defn new-event [{{:keys [service version]} :meta :as message} store]
  (prn "RECEIVED NEW-EVENT: " message)
  (storage.api/touch-service service version store)
  (-> (assoc-in message [:meta :received-at] (LocalDateTime/now))
      (storage.api/add-new-event store)))

(defn heartbeat [{{:keys [type service version]} :meta payload :payload} store]
  (storage.api/touch-service service version store)
  (prn "RECEIVED HEARTBEAT FROM" service " TYPE" type " PAYLOAD" payload))

(defn register [{{:keys [service version]} :meta :as message} store]
  (prn "RECEIVED REGISTER: " message)
  (storage.api/touch-service service version store))

(defn close [message store]
  (prn "RECEIVED CLOSE: " message)
  (storage.api/unregister-service (-> message :meta :service) store))

(def message-handlers {:new-event new-event
                       :heartbeat heartbeat
                       :register  register
                       :close     close})

(defn get-event-type [message]
  (-> message :meta :type keyword))

(defn dispatch-messages [message store]
  (when-let [handler (get message-handlers (get-event-type message))]
    (handler message store)))
