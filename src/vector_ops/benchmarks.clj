(ns vector-ops.benchmarks
  (use criterium.core)
  (import (hamt RT)))

(def data (vec (range 0 1000000)))

(defmacro measure [expr]
  `(with-progress-reporting (bench ~expr :verbose)))

(defn bench-concat-clj []
  (measure (into data data)))

(defn bench-concat-opt []
  (measure (RT/concat data data)))

(defn -main [& args]
  (bench-concat-clj))