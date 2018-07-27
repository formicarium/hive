(ns hive.syncthing.controller
  (:require [hive.syncthing.logic :as logic]
            [hive.utils :as utils]
            [hive.syncthing.syncthing-client :as syncthing.client]))

#_(def host "http://localhost:8384")
#_(def api-key "KSaEhq9zqjXkbEmwPgZ5ppKSo4GatgXp")
#_(def client (syncthing.client/new-syncthing-client host api-key))

(defn register-device [user-device-id {service-name :name {:keys [api-key device-id]} :syncthing}]
  (let [host (utils/tap (str "http://" (name service-name) ":8384"))
        client (utils/tap (syncthing.client/new-syncthing-client host api-key))
        folder {:path "app"}]
    (syncthing.client/add-device client {:device-id user-device-id :name "UserPaps"})
    (syncthing.client/add-folder client folder {:device-id user-device-id} {:device-id device-id})))
