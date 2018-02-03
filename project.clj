(defproject vector-ops "0.1.0"
  :description "Optimised vector operations"
  :url "https://github.com/AvramRobert/vector-ops"
  :license {:name "Apache-2.0 License"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :java-source-paths ["src/java"]
  ;:main vector-ops.benchmarks.core                          ;; comment out when uberjaring / publishing
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :cred :gpg}]]
  :profiles {:dev {:dependencies [[criterium "0.4.4"]
                                  [expectations "1.4.41"]
                                  [org.clojure/test.check "0.9.0"]]}
             :uberjar {:aot [vector-ops.core]}})
