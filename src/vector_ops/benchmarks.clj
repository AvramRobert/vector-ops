(ns vector-ops.benchmarks
  (use criterium.core)
  (require [vector-ops.core :as c]))

(def data (vec (range 0 1000000)))
(def spread-data (mapv (constantly data) (range 0 10)))
(def n 425166)
(def m 785221)
(def p #(< % n))
(def p2 #(> % n))

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

(defn bench-take-while-clj []
  (measure (vec (take-while p data))))

(defn bench-take-while-opt []
  (measure (c/takev-while p data)))

(defn bench-drop-while-clj []
  (measure (vec (drop-while p data))))

(defn bench-drop-while-opt []
  (measure (c/dropv-while p data)))

(defn bench-take-last-while-clj [] []
  (measure (->> (reverse data) (take-while p2) (reverse))))

(defn bench-take-last-while-opt [] []
  (measure (c/takev-last-while p2 data)))

(defn bench-drop-last-while-clj []
  (measure (->> (reverse data) (drop-while p2) (reverse))))

(defn bench-drop-last-while-opt []
  (measure (c/dropv-last-while p2 data)))

(defn bench-subvec-clj []
  (measure (subvec data n m)))

(defn bench-slice-clj []
  (measure (->> data (take m) (drop n) (vec))))

(defn bench-slice-opt []
  (measure (c/slicev data n m)))

(defn -main [& args]
  (bench-slice-opt))