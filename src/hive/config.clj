(ns hive.config
  (:require [clj-time.core :as t]))

(def unresponsive-threshold-s 30)
(def death-threshold-s 90)

(def healthcheck-timing-s 20)
