(ns hive.zmq
  (:require [zeromq.zmq :as zmq]
            [cheshire.core :as cheshire]
            [clojure.core.async :as async]))

(def context (zmq/context 1))

(defn bytes->string [^bytes bs]
  (when bs
    (String. bs)))

(defn str->bytes [str]
  (when str
    (.getBytes str)))

(defn port->endpoint [port]
  (str "tcp://localhost:" port))

(defn new-router-socket! [port]
  (let [router (zmq/socket context :router)]
    (zmq/set-receive-timeout router 1000)
    (zmq/set-send-timeout router 1000)
    (zmq/bind router (port->endpoint port))))

(defn new-delaer-socket! [endpoint ident]
  (let [dealer (zmq/socket context :dealer)]
    (zmq/set-identity dealer (str->bytes ident))
    (zmq/set-receive-timeout dealer 10000)
    (zmq/set-send-timeout dealer 1000)
    (zmq/connect dealer endpoint)))

(defn send! [socket & parts]
  (loop [[x & xs] parts]
    (when x
      (if xs
        (do (zmq/send socket (str->bytes x) zmq/send-more)
            (recur xs))
        (zmq/send socket (str->bytes x))))))

(defn send-message! [dealer message-map]
  (send! dealer (cheshire/generate-string (:meta message-map)) (cheshire/generate-string (:payload message-map))))

(defn send-response! [router message-map]
  (send! router (:identity message-map) (:meta message-map) (:payload message-map)))

(defn receive-all-str! [socket]
  (mapv bytes->string (zmq/receive-all socket)))

(defn receive-message! [socket]
  (let [[ident meta payload :as received] (receive-all-str! socket)]
    (when (every? some? received)
      {:identity ident
       :meta     (cheshire/parse-string meta true)
       :payload  (cheshire/parse-string payload true)})))

(defn send-channel [dealer]
  (let [ch (async/chan 1000)]
    [ch
     (async/go-loop []
       (some->> (async/<! ch)
                (send-message! dealer))
       (recur))]))

(defn receive-channel [router on-receive]
  (let [ch (async/chan 1000)]
    [ch
     (async/go-loop []
       (some->> (receive-message! router)
                (async/>! ch))
       (recur))
     (async/go-loop []
       (some-> (async/<! ch)
               (on-receive router))
       (recur))]))


(defn request-connection! [dealer]
  (send-message! dealer {:meta    {:type "request-connection"}
                         :payload {}})
  (zmq/receive dealer))

(defn handle-new-connection [router message]
  ;; DO REGISTRATION STUFF
  (send-response! router {:identity message :meta {} :payload}))

