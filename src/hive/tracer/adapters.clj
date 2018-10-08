(ns hive.tracer.adapters
  (:require [camel-snake-kebab.core :refer [->camelCase]]
            [cheshire.core :as cheshire]
            [clojure.walk :refer [postwalk]]))

(defn bytes->string [^bytes bs]
  (when bs
    (String. bs)))

(defn port->endpoint [port]
  (str "tcp://*:" port))

(defn raw-event->internal [[ident meta payload]]
  {:identity ident
   :meta     (cheshire/parse-string meta true)
   :payload  (cheshire/parse-string payload true)})

(defn str->bytes [str]
  (when str
    (.getBytes str)))

(defn externalize [event]
  (-> event
      (assoc :receivedAt (-> event :meta :received-at str))
      (dissoc :received-at)
      (update-in [:meta :type] keyword)
      (as-> %
          (if (= :new-event (get-in % [:meta :type]))
            (-> %
                (update-in [:payload :tags :direction] keyword)
                (update-in [:payload :tags :kind] keyword)
                (update-in [:payload :tags :type] keyword)
                (->> (postwalk #(if (keyword? %)
                                  (->camelCase %)
                                  %))))
            (dissoc % :payload)))))
