(ns hive.storage.store)

(def registered-services* (atom {}))
(def received-events* (atom []))

(defn register-new-service [service-name]
    (swap! registered-services* service-name {:last-timestamp 0}))
  
(defn unregister-service [service-name]
  (swap! registered-services* dissoc service-name))
  
(defn add-new-event [event]
  (swap! received-events* conj event))