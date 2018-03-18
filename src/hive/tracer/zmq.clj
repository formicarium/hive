(ns hive.tracer.zmq
  (:require [cheshire.core :as cheshire]
            [clojure.core.async :as async]
            [clj-time.core :as t]
            [com.stuartsierra.component :as component]
            [zeromq.zmq :as zmq]
            [hive.storage.store :as store]
            [hive.scheduler :as scheduler]))

(def context (zmq/context 1))

(defn bytes->string [^bytes bs]
  (when bs
    (String. bs)))

(defn port->endpoint [port]
  (str "tcp://*:" port))

(defn new-router-socket! [port]
  (let [router (zmq/socket context :router)]
    (zmq/set-receive-timeout router 1000)
    (zmq/set-send-timeout router 1000)
    (zmq/bind router (port->endpoint port))))

(defn receive-all-str! [socket]
  (mapv bytes->string (zmq/receive-all socket)))

(defn try-receive-message! [socket]
  (let [[ident meta payload :as received] (receive-all-str! socket)]
    (when (every? some? received)
      {:identity ident
       :meta     (cheshire/parse-string meta true)
       :payload  (cheshire/parse-string payload true)})))

(defn respond! [router message]
  (async/go (zmq/send router message)))

(defn service-status [service]
  (let [{last-timestamp :last-timestamp} (second service)]
    (prn "last update was : " last-timestamp)
    (condp t/after? last-timestamp
      (t/ago (t/seconds 40)) :dead
      (t/ago (t/seconds 23)) :unresponsive
      :guchi)))

(def update-fn {:guchi identity
                :unresponsive store/mark-as-unresponsive!
                :dead store/mark-as-dead!})

(defn service->service-name [service]
  (first (keys service)))

(defn healthcheck-services! []
  (doseq [[status service] (group-by service-status (store/get-unresponsive-services))]
    (prn "changing service: " service " to status: " status)
    (and (get update-fn status) (service->service-name service))))

(defn start-receiving! [router on-receive]
  (let [stop-channel (async/chan)
        heartbeat-ch (scheduler/heartbeat-ch 5)
        ch           (async/chan 1000)]
    (async/go-loop []
      (when (async/alt! stop-channel false :priority true :default :keep-going)
        (some->> (try-receive-message! router)
                 (async/>! ch))
        (recur)))
    (async/go-loop []
      (let [[source value] (async/alt! stop-channel [:stop]
                                       ch           ([v] [:ch v])
                                       heartbeat-ch ([v] [:heartbeat v]))]
        (case source
          :stop      (run! async/close! [stop-channel heartbeat-ch ch])
          :ch        (do (prn "DEFAULT TRIGGERED.")(on-receive value router) (recur))
          :heartbeat (do (prn "HEARTBEAT TRIGGERED.")(healthcheck-services!) (recur))
          (recur))))
    stop-channel))

(defn terminate-receiver-channel! [ch] (async/close! ch))
(defn terminate-router-socket! [router] (zmq/close router))

