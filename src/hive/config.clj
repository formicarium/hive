(ns hive.config
  (:require [clj-time.core :as t]))

(def main-ch-buffer-size 100)

(def unresponsive-threshold-s 30)
(def death-threshold-s 90)

(def healthcheck-timing-s 20)
