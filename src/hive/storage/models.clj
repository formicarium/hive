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

(s/defschema SyncthingConfig {:api-key   s/Str
                              :device-id s/Str})

(s/defschema Service {:name           s/Keyword
                      :syncthing      SyncthingConfig
                      :last-timestamp LocalDateTime
                      :version        s/Str
                      :status         ServiceStatuses})

(s/defschema State {:services {s/Keyword Service}
                    :events   [Event]})

(s/defschema Device {:device-id s/Str
                     :name s/Str})
