(defproject vector-ops "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :java-source-paths ["src/java"]
  :main vector-ops.benchmarks
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:dependencies [[criterium "0.4.4"]
                                  [expectations "1.4.41"]
                                  [org.clojure/test.check "0.9.0"]]}})
