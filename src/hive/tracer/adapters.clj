(ns hive.tracer.adapters
  (:require [cheshire.core :as cheshire]))

(defn service->service-name [service]
  (first (keys service)))

(defn identity->service-name [identity]
  (keyword identity))

(defn bytes->string [^bytes bs]
  (when bs
    (String. bs)))

(defn port->endpoint [port]
  (str "tcp://*:" port))

(defn raw-event->internal [[ident meta payload]]
  {:identity ident
   :meta     (cheshire/parse-string meta true)
   :payload  (cheshire/parse-string payload true)}) 
