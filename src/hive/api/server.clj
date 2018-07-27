(ns hive.api.server
  (:require [io.pedestal.http :as http]
            [hive.api.service :as api.service]))

(defn run
  "The entry-point for 'lein run-dev'"
  [store]
  (println "\nCreating your [DEV] server...")
  (-> (api.service/service store)
      (merge {:env                   :dev
              ::http/join?           true
              ::http/port            8080
              ::http/allowed-origins {:creds           true
                                      :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      http/default-interceptors
      http/dev-interceptors
      http/create-server
      http/start))
