(ns hive.syncthing.config
  (:require [clojure.xml :as xml]
            [hive.syncthing.constants :as constants]))

(defn read-st-config!
  [path]
  (xml/parse path))

(def st-config (atom ""))

(defn reset-st-config!
  [path]
  (->>
       (reset! st-config "")))
