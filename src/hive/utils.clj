(ns hive.utils
  (:require clojure.pprint))

(defn tap [x] (clojure.pprint/pprint x) x)
