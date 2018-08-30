(ns hive.storage.models
  (:require [schema.core :as s])
  (:import java.time.LocalDateTime))

(s/defschema MessageTypes (s/enum :new-event :heartbeat :test))
(s/defschema EventTypes (s/enum :kafka :http))
(s/defschema EventDirection (s/enum :in-request :in-response :out-request :out-response))

(s/defschema Meta {:type      EventTypes
                   :timestamp LocalDateTime
                   :service   s/Keyword})

(s/defschema PimbaEvent {:data {s/Keyword s/Any}
                         :type EventTypes
                         :direction EventDirection})

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

;; Graphs & cia
(s/defschema NodeType (s/enum :consumer :service))

(s/defschema Node {:id s/Str
                   :name s/Str
                   :type NodeType})

(s/defschema Edge {:source Node
                   :target Node
                   :event Event})

(s/defschema Graph {:nodes [Node]
                    :edges [Edge]})
