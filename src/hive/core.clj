(ns hive.core
  (:require [zeromq.zmq :as zmq]))

(defn new-server! [port]
  (let [context (zmq/context 1)
        router  (zmq/socket context :router)]
    (zmq/set-receive-timeout router 1000)
    (zmq/bind router (str "tcp://*:" port))))

(defn terminate-server! [server]
  (zmq/close server))
