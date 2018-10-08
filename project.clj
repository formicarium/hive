(defproject hive "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :repositories [["sonatype" {:url    "https://oss.sonatype.org/content/repositories/snapshots"
                              :update :always}]]
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [leoiacovini/clj-service "1.1.6"]
                 [org.clojure/core.async "0.4.474"]
                 [cheshire "5.8.0"]
                 [clj-http "3.9.0"]
                 [org.zeromq/jeromq "0.4.3"]
                 [org.zeromq/cljzmq "0.1.5-SNAPSHOT" :exclusions [org.zeromq/jzmq]]
                 [prismatic/schema "1.1.7"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [com.walmartlabs/lacinia "0.29.0-rc-1"]
                 [com.walmartlabs/lacinia-pedestal "0.10.0-rc-1"]
                 [camel-snake-kebab "0.4.0"]]
  :resource-paths ["resources" "config"]
  :min-lein-version "2.0.0"
  :profiles {:dev     {:aliases      {"run-dev" ["trampoline" "run" "-m" "hive.core/start!"]}
                       :plugins      [[lein-midje "3.2.1"]]
                       :dependencies [[io.pedestal/pedestal.service-tools "0.5.3"]
                                      [midje "1.9.1"]]}
             :uberjar {:aot [hive.core]}}
  :main ^{:skip-aot true} hive.core)
