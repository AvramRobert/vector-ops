(ns vector-ops.benchmarks.core
  (require [vector-ops.benchmarks.bench :as b]
           [vector-ops.benchmarks.markdown :as m]))

(defn -main [& args]
  (letfn [(eps [] (println "Benchmark not specified or unknown. Nothing to benchmark."))]
    (case (first args)
      "benchmark" (-> (second args)
                      (keyword)
                      (b/benchmarks)
                      (apply []))
      "make-page" (spit "./benchmarks/benchmark.md" (m/page)))))
