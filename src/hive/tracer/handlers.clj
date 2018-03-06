(ns hive.tracer.handler
    (:require [hive.storage.store :as store]
              [hive.tracer.zmq :as tracer.zmq]))

(defn new-event [message router]
    (prn "RECEIVED NEW-EVENT: " message)
    (store/add-new-event (:payload message)))

(defn heartbeat [message router]
    (prn "RECEIVED HEARTBEAT: " message)
    (tracer.zmq/respond! router {}))

(def register [message router]
    (prn "RECEIVED REGISTER: " message)
    (store/register-new-service (-> message :meta :name keyword)))

(def close [message router]
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