(ns hive.syncthing.models
  (:require [schema.core :as s]))

(s/defschema Device {:deviceID    s/Str
                     :name        s/Str
                     :addresses   [s/Str]
                     :introducer  s/Bool
                     :certName    s/Str
                     :compression s/Str})

(s/defschema Folder {:path                  s/Str
                     :pullerPauseS          s/Int
                     :pullers               s/Int
                     :type                  s/Str
                     :invalid               s/Str
                     :disableTempIndexes    s/Bool
                     :ignorePerms           s/Bool
                     :disableSparseFiles    s/Bool
                     :maxConflicts          s/Int
                     :fsync                 s/Bool
                     :scanProgressIntervalS s/Int
                     :label                 s/Str
                     :id                    s/Str
                     :hashers               s/Int
                     :pullerSleepS          s/Int
                     :devices               [Device]
                     :order                 s/Str
                     :autoNormalize         s/Bool
                     :copiers               s/Int
                     :rescanIntervalS       s/Int
                     :ignoreDelete          s/Bool
                     :versioning            {:type   s/Str
                                             :params {:keep s/Str}}
                     :minDiskFreePct        s/Int})
