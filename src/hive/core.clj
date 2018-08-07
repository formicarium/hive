(ns hive.core
  (:gen-class)
  (:require [hive.components :as components]))

(defn start! []
  (components/start-system!))

(defn -main [& args]
  (start!))
