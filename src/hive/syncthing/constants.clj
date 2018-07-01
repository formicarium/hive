(ns hive.syncthing.constants)

; Constants
(def st-config-path (str (System/getProperty "user.home") "/" ".syncthing/config.xml"))
(def st-host "http://localhost:8384")
(def bookmark
  {:config (str st-host "/rest/system/config")
   :status (str st-host "/rest/system/status")})


