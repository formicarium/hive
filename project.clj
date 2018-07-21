(defproject hive "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :repositories [["sonatype" {:url    "https://oss.sonatype.org/content/repositories/snapshots"
                              :update :always}]]
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474"]
                 [cheshire "5.8.0"]
                 [clj-http "3.9.0"]
                 [org.zeromq/jeromq "0.4.3"]
                 [org.zeromq/cljzmq "0.1.5-SNAPSHOT" :exclusions [org.zeromq/jzmq]]
                 [io.pedestal/pedestal.service "0.5.3"]
                 [io.pedestal/pedestal.service-tools "0.5.3"]
                 [io.pedestal/pedestal.jetty "0.5.3"]
                 [prismatic/schema "1.1.7"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.walmartlabs/lacinia "0.25.0"]
                 [com.walmartlabs/lacinia-pedestal "0.7.0"]]
  :resource-paths ["resources" "config"]
  :min-lein-version "2.0.0"
  :profiles {:dev     {:aliases      {"run-dev" ["trampoline" "run" "-m" "hive.core/start!"]}
                       :dependencies [[io.pedestal/pedestal.service-tools "0.5.3"]]}
             :uberjar {:aot :all}}
  :main ^{:skip-aot true} hive.core)
