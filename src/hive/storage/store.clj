(ns hive.storage.store
  (:require [com.stuartsierra.component :as component]))

(defprotocol StateStore
  (get-state [this]))

(defrecord Store [initial-state]
  component/Lifecycle
  (start [this]
    (let [state (atom (or initial-state {}))]
      (assoc this :state state)))

  (stop [this]
    (dissoc this :state))

  StateStore
  (get-state [this] (:state this)))

(defn new-store
  ([initial-state]
   (component/start (->Store initial-state)))
  ([]
   (new-store nil)))

(defn terminate-store [store]
  (component/stop store))
