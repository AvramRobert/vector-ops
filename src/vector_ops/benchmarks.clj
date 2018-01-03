(ns vector-ops.benchmarks
  (use criterium.core)
  (require [vector-ops.core :as c]))

(def data (vec (range 0 1000000)))
(def spread-data (mapv (constantly data) (range 0 10)))

(defmacro measure [expr]
  `(with-progress-reporting (bench ~expr :verbose)))

(defn bench-concat-clj []
  (measure (into data data)))

(defn bench-concat-opt []
  (measure (c/concatv data data)))

(defn bench-concat-spread-opt []
  (measure (apply c/concatv spread-data)))

(defn bench-mapv-clj []
  (measure (mapv inc data)))

(defn bench-mapv-opt []
  (measure (c/mapv inc data)))

(defn bench-take-clj []
  (measure (vec (take 425166 data))))

(defn bench-take-opt []
  (measure (c/takev 425166 data)))

(defn -main [& args]
  (bench-take-opt))