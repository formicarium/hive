(ns hive.syncthing.constants)

; Constants
(def st-config-path (str "/Users/rafaelribeirocorreia/dev/formicarium/fmc/device2/syncthing-cfg/config.xml"))

(def st-host "http://localhost:8386")
(def bookmark
  {:config (str st-host "/rest/system/config")
   :status (str st-host "/rest/system/status")})


