(ns hive.syncthing.request
  (:require [hive.components.http :as http]
            [hive.syncthing.config :as config]
            [hive.syncthing.logic :as logic]
            [hive.syncthing.constants :as constants])
  (:use [slingshot.slingshot :only [try+ throw+]]))

(def tries (atom 0))

(defn authd-req!
  [{:keys [url method options] :as params}]
  (try+
    (swap! tries inc)
    (let [response (http/raw-req! {:url     url
                                   :method  method
                                   :options (merge (logic/get-base-options (logic/get-api-key-from-st-config-memo @config/st-config)) options)})]
      (reset! tries 0)
      response)
    (catch [:status 403] err
      (print "error 403")
      (config/read-st-config! constants/st-config-path)
      (if (< @tries 3)
        (authd-req! params)
        (throw+ err)))
    (catch [:status 400] err
      (print "400")
      (clojure.pprint/pprint err))))

