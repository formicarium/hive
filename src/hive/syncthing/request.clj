(ns hive.syncthing.request
  (:require [hive.components.http :as http]
            [hive.syncthing.config :as config]
            [hive.syncthing.logic :as logic]
            [hive.syncthing.constants :as constants])
  (:use [slingshot.slingshot :only [try+]]))

(defn authd-req!
  [{:keys [url method options] :as params}]
  (try+
    (http/raw-req! {:url     url
                    :method  method
                    :options (merge (logic/get-base-options (logic/get-api-key-from-st-config-memo @config/st-config)) options)})
    (catch [:status 403] {}
      (print "error 403")
      (config/read-st-config! constants/st-config-path)
      (authd-req! params))))
