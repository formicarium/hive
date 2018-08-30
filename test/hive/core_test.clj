(ns hive.core-test
  (:require [clojure.test :refer :all]
            [hive.api.resolvers.queries.graph :as g]
            [hive.core :refer :all])
  (:import java.time.LocalDateTime))

(deftest nodes-test
  (testing "pega um node ae"
    (is (= {:id   "tyrion"
            :name "tyrion"
            :type :service}
           (g/event->node {:identity "tyrion"
                           :meta     {:type      :new-event
                                      :timestamp (LocalDateTime/of 2018 01 01 12 0 0)
                                      :service   :tyrion}
                           :payload  {:type      :http
                                      :direction :out-request
                                      :data {:eae :men}}}))))

  (testing "pega os node ae"
    (let [events [{:identity "tyrion"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 0)
                              :service   :tyrion}
                   :payload  {:type      :http
                              :direction :out-request
                              :data {:eae :men}}}
                  {:identity "sr-barriga"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 1)
                              :service   :sr-barriga}
                   :payload {:type :http
                             :direction :in-request
                             :data {:eae :men}}}
                  {:identity "sr-barriga"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 2)
                              :service   :sr-barriga}
                   :payload {:type :http
                             :direction :in-response
                             :data {:eae :men}}}
                  {:identity "tyrion"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 3)
                              :service   :tyrion}
                   :payload  {:type      :http
                              :direction :out-response
                              :data {:eae :men}}}
                  {:identity "tyrion"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 4)
                              :service   :tyrion}
                   :payload  {:type      :kafka
                              :direction :producer
                              :data {:endpoint "debt-updated"}}}
                  {:identity "line-items"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 5)
                              :service   :line-items}
                   :payload  {:type      :kafka
                              :direction :consumer
                              :data {:falou :mana}}}]
          expected-nodes #{{:id   "tyrion"
                            :name "tyrion"
                            :type :service}
                           {:id   "sr-barriga"
                            :name "sr-barriga"
                            :type :service}
                           {:id   "debt-updated"
                            :name "debt-updated"
                            :type :topic}
                           {:id   "line-items"
                            :name "line-items"
                            :type :service}}]
      (is (= expected-nodes
             (g/events->nodes events))))))

(deftest edges-test
  (testing "pega os edge ae"
    (let [events [{:identity "tyrion"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 0)
                              :service   :tyrion}
                   :payload  {:type      :http
                              :direction :out-request
                              :data {:eae :men}}}
                  {:identity "sr-barriga-supimpa"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 1)
                              :service   :sr-barriga}
                   :payload {:type :http
                             :direction :in-request
                             :data {:eae :men}}}
                  {:identity "sr-barriga"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 2)
                              :service   :sr-barriga}
                   :payload {:type :http
                             :direction :in-response
                             :data {:eae :men}}}
                  {:identity "tyrion"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 3)
                              :service   :tyrion}
                   :payload  {:type      :http
                              :direction :out-response
                              :data {:eae :men}}}
                  {:identity "tyrion"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 4)
                              :service   :tyrion}
                   :payload  {:type      :kafka
                              :direction :producer
                              :data {:eae :men}}}
                  {:identity "line-items"
                   :meta     {:type      :new-event
                              :timestamp (LocalDateTime/of 2018 01 01 12 0 5)
                              :service   :line-items}
                   :payload  {:type      :kafka
                              :direction :consumer
                              :data {:falou :mana}}}]
          expected-edges [{:source {:id   "tyrion"}
                           :target {:id   "sr-barriga"}
                           :event  {:identity "tyrion"
                                    :meta     {:type      :new-event
                                               :timestamp (LocalDateTime/of 2018 01 01 12 0 0)
                                               :service   :tyrion}
                                    :payload  {:type      :http
                                               :direction :out-request
                                               :data {:eae :men}}}}

                          {:source {:id   "sr-barriga"}
                           :target {:id   "tyrion"}
                           :event  {:identity "sr-barriga"
                                    :meta     {:type      :new-event
                                               :timestamp (LocalDateTime/of 2018 01 01 12 0 1)
                                               :service   :sr-barriga}
                                    :payload {:type :http
                                              :direction :in-request
                                              :data {:eae :men}}}}

                          {:source {:id   "sr-barriga"}
                           :target {:id   "tyrion"}
                           :event  {:identity "sr-barriga"
                                    :meta     {:type      :new-event
                                               :timestamp (LocalDateTime/of 2018 01 01 12 0 2)
                                               :service   :sr-barriga}
                                    :payload {:type :http
                                              :direction :in-response
                                              :data {:eae :men}}}}

                          {:source {:id   "tyrion"}
                           :target {:id   "sr-barriga"}
                           :event  {:identity "tyrion"
                                    :meta     {:type      :new-event
                                               :timestamp (LocalDateTime/of 2018 01 01 12 0 3)
                                               :service   :tyrion}
                                    :payload  {:type      :http
                                               :direction :out-response
                                               :data {:eae :men}}}}

                          {:source {:id   "tyrion"}
                           :target {:id   "debt-updated"}
                           :event  {:identity "tyrion"
                                    :meta     {:type      :new-event
                                               :timestamp (LocalDateTime/of 2018 01 01 12 0 4)
                                               :service   :tyrion}
                                    :payload  {:type      :kafka
                                               :direction :producer
                                               :data {:eae :men}}}}

                          {:source {:id   "debt-updated"}
                           :target {:id   "line-items"}
                           :event  {:identity "line-items"
                                    :meta     {:type      :new-event
                                               :timestamp (LocalDateTime/of 2018 01 01 12 0 5)
                                               :service   :line-items}
                                    :payload  {:type      :kafka
                                               :direction :consumer
                                               :data {:falou :mana}}}}


                          ]]
      (is (= expected-edges
             (g/events->edges events))))))


                                        ;tyrion -> debt-updated
                                        ;debt-updated -> sr-barriga
                                        ;debt-updated -> line-items
                                        ;tyrion -> sr-barriga (out-request)
                                        ;sr-barriga (in-request) from tyrion
                                        ;sr-barriga (out-response) to tyrion
                                        ;tyrion -. sr-barriga (out-response)
