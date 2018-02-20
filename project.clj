(defproject hive "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :repositories [["sonatype" {:url "https://oss.sonatype.org/content/repositories/snapshots"
                             :update :always}]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474"]
                 [cheshire "5.8.0"]
                 [org.zeromq/jeromq "0.4.3"]
                 [org.zeromq/cljzmq "0.1.5-SNAPSHOT" :exclusions [org.zeromq/jzmq]]
                 [com.stuartsierra/component "0.3.2"]])
