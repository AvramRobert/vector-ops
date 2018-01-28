(defproject vector-ops "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache-2.0 License"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :java-source-paths ["src/java"]
  :main vector-ops.benchmarks.core
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:dependencies [[criterium "0.4.4"]
                                  [expectations "1.4.41"]
                                  [org.clojure/test.check "0.9.0"]]}})
