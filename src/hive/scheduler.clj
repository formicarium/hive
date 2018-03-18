(ns hive.scheduler
  (:require [chime :refer [chime-ch]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]))

(defn heartbeat-ch [seconds]
  (chime-ch (rest (periodic-seq (t/now) (t/seconds seconds)))))
