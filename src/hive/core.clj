(ns hive.core
  (:require [hive.zmq]
            [clojure.core.async :as async])
  (:import (java.util Date)))

(def registered-services* (atom {}))
(def healthcheck-interval 15000)
(def healthcheck-threshold 60000)

(defn register-new-service! [service-name]
  (swap! registered-services* service-name {:last-timestamp (Date.)}))

(defn unregister-service [service-name]
  (swap! registered-services* dissoc service-name))

(defn publish-by-event [message]
  (-> message :meta :type keyword))

(defn publish-by-sender [message]
  (-> message :identity keyword))

(defn create-publisher [channel by]
  (async/pub channel by))

(def event-handlers {:new-event (fn [_ _] )
                     :heartbeat (fn [_ _] )
                     :register  (fn [_ _] )
                     :close     (fn [_ _] )})

(defn healthcheck [service]
  (async/go-loop []

    (recur)))

