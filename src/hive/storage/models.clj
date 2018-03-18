(ns hive.storage.models
  (:require [schema.core :as s])
  (:import java.time.LocalDateTime))

(def EventTypes (s/enum :new :heartbeat :test))
(def PortTypes (s/enum :http :kafka))

(def Event {:identity s/Str
            :meta     {:type         EventTypes
                       :synchronous? s/Bool ;may not be useful
                       :port         PortTypes}
            :payload  s/Str})

(def ServiceStatuses (s/enum :ok :unresponsive :dead))

(def Service {:name           s/Keyword
              :last-timestamp LocalDateTime
              :status         ServiceStatuses})
