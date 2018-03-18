(ns hive.scheduler
  (:require [chime :refer [chime-ch]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.core.async :as async :refer [<! go-loop]]
            [hive.storage.store :as store]))

(defn heartbeat-ch [seconds]
  (chime-ch (rest (periodic-seq (t/now) (t/seconds seconds)))))
