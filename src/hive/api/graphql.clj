(ns hive.api.graphql
  (:require [com.walmartlabs.lacinia.schema :as schema]))

(def definitions
  {})

(def queries
  {:queries {:hello {:type    'String
                     :resolve (constantly "world")}}})

(def mutations
  {})

(def Schema (schema/compile (merge definitions queries mutations)))
