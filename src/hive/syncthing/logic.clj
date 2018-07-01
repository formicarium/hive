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