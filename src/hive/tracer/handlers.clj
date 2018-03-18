(ns hive.tracer.handlers
  (:require [hive.storage.store :as store]
            [hive.tracer.adapters :as adapters]))

(defn new-event [message router]
  (prn "RECEIVED NEW-EVENT: " message)
  (store/add-new-event (:payload message)))

(defn heartbeat [{{:keys [type port]} :meta payload :payload identity :identity :as message} router]
  (some->> identity
           adapters/identity->service-name
           store/touch-service
           (prn "RECEIVED HEARTBEAT FROM : " identity " TYPE: " type " PORT: " port " PAYLOAD: " payload))
  #_(tracer.zmq/respond! router {}))

(defn register [message router]
  (prn "RECEIVED REGISTER: " message)
  (store/register-new-service (-> message :meta :name keyword)))

(defn close [message router]
  (prn "RECEIVED CLOSE: " message)
  (store/unregister-service (-> message :meta :name keyword)))

(def message-handlers {:new-event new-event
                        :heartbeat heartbeat
                        :register  register
                        :close     close})

(defn get-event-type [message]
  (-> message :meta :type keyword))

(defn dispatch-messages [message router]
  (when-let [handler (get message-handlers (get-event-type message))]
    (handler message router)))
