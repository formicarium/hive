(ns hive.syncthing.models
  (:require [schema.core :as s]))


(s/defschema Device {:addresses   [s/Str]
                     :certName    s/Str
                     :compression s/Str
                     :deviceID    s/Str
                     :introducer  s/Bool
                     :name        s/Str})

(s/defschema MinimalDevice (select-keys Device [:deviceID]))

(s/defschema Folder {:$$hashKey s/Str,
                     :autoNormalize s/Bool,
                     :copiers s/Int,
                     :devices [MinimalDevice],
                     :disableSparseFiles s/Bool,
                     :disableTempIndexes s/Bool,
                     :filesystemType s/Str,
                     :fsWatcherDelayS s/Int,
                     :fsWatcherEnabled s/Bool,
                     :hashers s/Int,
                     :id s/Str,
                     :ignoreDelete s/Bool,
                     :ignorePerms s/Bool,
                     :label s/Str,
                     :markerName s/Str,
                     :maxConflicts s/Int,
                     :minDiskFree {:value s/Int, :unit s/Str},
                     :order s/Str,
                     :path s/Str,
                     :paused s/Bool,
                     :pullerMaxPendingKiB s/Int,
                     :pullerPauseS s/Int,
                     :rescanIntervalS s/Int,
                     :scanProgressIntervalS s/Int,
                     :type s/Str,
                     :useLargeBlocks s/Bool,
                     :versioning {:type s/Str, :params {s/Str s/Str}},
                     :weakHashThresholdPct s/Int})

(s/defschema FolderRequest {:autoNormalize s/Bool,
                            :devices [MinimalDevice],
                            :externalCommand s/Str,
                            :fileVersioningSelector s/Str,
                            :fsWatcherDelayS s/Int,
                            :fsWatcherEnabled s/Bool,
                            :fsync s/Bool,
                            :id s/Str,
                            :label s/Str,
                            :maxConflicts s/Int,
                            :minDiskFree {:value s/Int, :unit s/Str},
                            :order s/Str,
                            :path s/Str,
                            :rescanIntervalS s/Int,
                            :simpleKeep s/Int,
                            :staggeredCleanInterval s/Int,
                            :staggeredMaxAge s/Int,
                            :staggeredVersionsPath s/Str,
                            :trashcanClean s/Int,
                            :type s/Str})
