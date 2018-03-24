(ns hive.storage.models
  (:require [schema.core :as s])
  (:import java.time.LocalDateTime))

(s/defschema EventTypes (s/enum :new :heartbeat :test))
(s/defschema PortTypes (s/enum :http :kafka))

(s/defschema Event {:identity s/Str
                    :meta     {:type EventTypes
                               :port PortTypes}
                    :payload  s/Str})

(s/defschema ServiceStatuses (s/enum :ok :unresponsive :dead))

(s/defschema Service {:name           s/Keyword
                      :last-timestamp LocalDateTime
                      :status         ServiceStatuses})

(s/defschema State {:services {s/Keyword Service}
                    :events   [Event]})
