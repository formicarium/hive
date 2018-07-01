(ns hive.syncthing.logic)


(defn add-device
  [config device]
  (print device)
  (update config :devices #(conj % device)))


(defn remove-device-by-name
  [config name]
  (update config :devices (fn [devices]
                            (remove #(= (:name %) name) devices))))

(defn add-folder [config folder]
  (update config :folders #(conj % folder)))

(defn get-api-key-from-st-config
  "improve this"
  [st-config]
  (print "heavy")
  (get-in st-config [:content 2 :content 1 :content 0]))

(def get-api-key-from-st-config-memo (memoize get-api-key-from-st-config))

(defn get-base-options [api-key] {:headers {"X-API-Key" api-key}
                                  :as      :json})