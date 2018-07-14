(ns hive.syncthing.config
  (:require [clojure.xml :as xml]
            [hive.syncthing.constants :as constants]))

(defn read-st-config!
  [path]
  (xml/parse path))

(def st-config (atom (read-st-config! constants/st-config-path)))

(defn reset-st-config!
  [path]
  (->> path
       (read-st-config!)
       (reset! st-config)))
