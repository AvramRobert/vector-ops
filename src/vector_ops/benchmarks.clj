(ns vector-ops.benchmarks
  (use criterium.core)
  (require [vector-ops.core :as c]))

(def data (vec (range 0 1000000)))
(def spread-data (mapv (constantly data) (range 0 10)))
(def n 425166)

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
  (measure (vec (take n data))))

(defn bench-take-opt []
  (measure (c/takev n data)))

(defn bench-drop-clj []
  (measure (vec (drop n data))))

(defn bench-drop-opt []
  (measure (c/dropv n data)))

(defn bench-split-clj []
  (measure (mapv vec (split-at n data))))

(defn bench-split-opt []
  (measure (c/splitv-at n data)))

(defn -main [& args]
  (bench-split-opt))