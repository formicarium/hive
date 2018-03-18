(ns hive.storage.models
  (:require [schema.core :as s]))

(def EventTypes (s/enum :new :heartbeat :test))
(def PortTypes (s/enum :http :kafka))

(def Event {:identity s/Str
            :meta     {:type         EventTypes
                       :synchronous? s/Bool ;may not be useful
                       :port         PortTypes}
            :payload  s/Str})
