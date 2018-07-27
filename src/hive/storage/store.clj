(ns hive.storage.store
  (:require [com.stuartsierra.component :as component]
            [hive.utils :as utils])
  (:import java.time.LocalDateTime))

(defprotocol StateStore
  (get-state [this]))

(defrecord Store [initial-state]
  component/Lifecycle
  (start [this]
    (let [state (atom (or initial-state (utils/tap {:services {"mancini" {:name           "mancini"
                                                                          :syncthing      {:api-key   "KSaEhq9zqjXkbEmwPgZ5ppKSo4GatgXp"
                                                                                           :device-id "MQDJBTV-ENVZY34-IQF53LC-XVD5XS4-HNHW766-VEN5ASZ-I5CZA5F-W3TPDAE"}
                                                                          :last-timestamp (LocalDateTime/now)
                                                                          :version        "1"
                                                                          :status         :ok}}
                                                    :events   []})))]
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
