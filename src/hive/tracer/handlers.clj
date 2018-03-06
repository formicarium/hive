(ns hive.tracer.handler)

(defn get-event-type [message]
    (-> message :meta :type keyword))

(def message-handlers {:new-event (fn [message _] (prn "RECEIVED NEW-EVENT: " message))
                       :heartbeat (fn [message _] (prn "RECEIVED HEARTBEAT: " message))
                       :register  (fn [message _] (prn "RECEIVED REGISTER: " message))
                       :close     (fn [message _] (prn "RECEIVED CLOSE: " message))})

(defn dispatch-messages [message router]
  ((get message-handlers (get-event-type message)) message router))