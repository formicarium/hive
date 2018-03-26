(ns hive.storage.models
  (:require [schema.core :as s])
  (:import java.time.LocalDateTime))

(s/defschema EventTypes (s/enum :new-event :heartbeat :test))

(s/defschema Meta {:type      EventTypes
                   :timestamp LocalDateTime
                   :service   s/Keyword})

(s/defschema Payload {s/Keyword s/Any})

(s/defschema Event {:identity s/Str
                    :meta     Meta
                    :payload  Payload})

(s/defschema ServiceStatuses (s/enum :ok :unresponsive :dead))

(s/defschema Service {:name           s/Keyword
                      :last-timestamp LocalDateTime
                      :version        s/Str
                      :status         ServiceStatuses})

(s/defschema State {:services {s/Keyword Service}
                    :events   [Event]})
