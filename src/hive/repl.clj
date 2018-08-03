(ns hive.repl
  (:require [clojure.tools.nrepl.server :as nrepl.server]
            [com.stuartsierra.component :as component]))

(defrecord ReplServer [port]
  component/Lifecycle
  (start [this]
    (assoc this :server (nrepl.server/start-server :port port)))
  (stop [this]
    (nrepl.server/stop-server (:server this))
    (dissoc this :server)))

(defn new-repl-server! [port] (component/start (->ReplServer port)))
