(ns hive.storage.models
  (:require [schema.core :as s])
  (:import java.time.LocalDateTime))

(s/defschema EventTypes (s/enum :new-event :heartbeat :test))

(s/defschema Meta {:type      EventTypes
                   :timestamp LocalDateTime
                   :service   s/Keyword})

(s/defschema HttpTags {:method s/Str
                       (s/optional-key :status_code) s/Int
                       :url s/Str})

(s/defschema Peer {:port s/Int
                   (s/optional-key :service) (s/maybe s/Str)})

(s/defschema SpanDirection (s/enum :consumer :producer))
(s/defschema SpanType (s/enum :http-in :http-out :kafka))
(s/defschema SpanKind (s/enum :start :end))

(s/defschema SpanTags {:http HttpTags
                       :direction SpanDirection
                       :peer Peer
                       :type SpanType
                       :kind SpanKind})

(s/defschema SpanContext {:trace-id s/Str
                          :span-id s/Str
                          :parent-id s/Str})

(s/defschema SpanPayload (s/constrained map?))

(s/defschema Span {:timestamp LocalDateTime
                   :tags SpanTags
                   (s/optional-key :payload) SpanPayload
                   :context SpanContext})

(s/defschema Payload Span)

(s/defschema Event {:identity s/Str
                    :meta     Meta
                    :payload  Payload})

(s/defschema ServiceStatuses (s/enum :ok :unresponsive :dead))

(s/defschema Service {:name           s/Keyword
                      :syncthing      SyncthingConfig
                      :last-timestamp LocalDateTime
                      :version        s/Str
                      :status         ServiceStatuses})

(s/defschema State {:services {s/Keyword Service}
                    :events   [Event]})

(s/defschema Device {:device-id s/Str
                     :name s/Str})
