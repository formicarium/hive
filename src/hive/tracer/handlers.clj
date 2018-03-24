(ns hive.tracer.handlers
  (:require [hive.storage.api :as storage.api]
            [hive.tracer.adapters :as adapters]))

(defn new-event [message store]
  (prn "RECEIVED NEW-EVENT: " message)
  (storage.api/touch-service (-> message :meta :service) store)
  (storage.api/add-new-event (:payload message) store))

(defn heartbeat [{{:keys [type service]} :meta payload :payload} store]
  (storage.api/touch-service service store)
  (prn "RECEIVED HEARTBEAT FROM : " service " TYPE: " type " PAYLOAD: " payload))

(defn register [message store]
  (prn "RECEIVED REGISTER: " message)
  (storage.api/touch-service (-> message :meta :service) store))

(defn close [message store]
  (prn "RECEIVED CLOSE: " message)
  (storage.api/unregister-service (-> message :meta :name keyword) store))

(def message-handlers {:new-event new-event
                       :heartbeat heartbeat
                       :register  register
                       :close     close})

(defn get-event-type [message]
  (-> message :meta :type keyword))

(defn dispatch-messages [message store]
  (when-let [handler (get message-handlers (get-event-type message))]
    (handler message store)))
