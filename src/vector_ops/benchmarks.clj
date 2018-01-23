(ns vector-ops.benchmarks
  (require [vector-ops.core :as o]
           [clojure.string :as s]
           [criterium.core :as c])
  (:import (java.io Writer StringWriter)))

(def data (vec (range 0 1000000)))
(def spread-data (mapv (constantly data) (range 0 10)))
(def n 425166)
(def m 785221)
(def p #(< % n))
(def p2 #(> % n))

(def data-ranges [0 10 100 1000 10000 100000 1000000])

(defmacro measure [expr]
  `(c/with-progress-reporting (c/bench ~expr :os)))

(defn extract-estimate [result]
  (let [s (re-find #"[0-9]+.[0-9]+ s" result)
        ms (re-find #"[0-9]+.[0-9]+ ms" result)
        micro (re-find #"[0-9]+.[0-9]+ .s" result)
        ns (re-find #"[0-9]+.[0-9]+ ns" result)
        num #(->> % (drop-last 2) (apply str) (Double/parseDouble))]
    (cond
      s  (-> (num s) (* 1000))
      ns (-> (num ns) (/ 1000000))
      ms (num ms)
      :else (-> (num micro) (/ 1000)))))

(defn extract-duration [writer]
  (-> (str writer)
      (s/split-lines)
      (nth 2)
      (extract-estimate)))

(defn run-benchmark [{:keys [name ranges expr]}]
  (->> ranges
       (reduce
         (fn [benchmarks i]
           (let [writer (StringWriter.)
                 _ (println "Running benchmark for " name " with " i " elements.")]
             (binding [*out* writer]
               (let [data (vec (range 0 i))
                     val  (if (zero? i) 0 (rand-nth data))
                     _    (c/bench (expr data val) :os)
                     ms   (extract-duration writer)]
                 (assoc benchmarks i ms))))) {})
       (mapv #(s/join "," %))
       (s/join "\n")
       (spit (str name "-benchmark.csv"))))

(defn bench-concat-clj []
  (run-benchmark {:name "clj-concat"
                  :ranges data-ranges
                  :expr (fn [data _] (into data data))}))

(defn bench-concat-opt []
  (run-benchmark {:name "opt-concat"
                  :ranges [data-ranges]
                  :expr (fn [data _] (o/concatv data data))}))

(defn bench-concat-spread-opt []
  (measure (apply o/concatv spread-data)))

(defn bench-mapv-clj []
  (measure (mapv inc data)))

(defn bench-mapv-opt []
  (measure (o/mapv inc data)))

(defn bench-take-clj []
  (measure (vec (take n data))))

(defn bench-take-opt []
  (measure (o/takev n data)))

(defn bench-drop-clj []
  (measure (vec (drop n data))))

(defn bench-drop-opt []
  (measure (o/dropv n data)))

(defn bench-split-clj []
  (measure (mapv vec (split-at n data))))

(defn bench-split-opt []
  (measure (o/splitv-at n data)))

(defn bench-take-while-clj []
  (measure (vec (take-while p data))))

(defn bench-take-while-opt []
  (measure (o/takev-while p data)))

(defn bench-drop-while-clj []
  (measure (vec (drop-while p data))))

(defn bench-drop-while-opt []
  (measure (o/dropv-while p data)))

(defn bench-take-last-while-clj [] []
  (measure (->> (reverse data) (take-while p2) (reverse))))

(defn bench-take-last-while-opt [] []
  (measure (o/takev-last-while p2 data)))

(defn bench-drop-last-while-clj []
  (measure (->> (reverse data) (drop-while p2) (reverse))))

(defn bench-drop-last-while-opt []
  (measure (o/dropv-last-while p2 data)))

(defn bench-subvec-clj []
  (measure (subvec data n m)))

(defn bench-slice-clj []
  (measure (->> data (take m) (drop n) (vec))))

(defn bench-slice-opt []
  (measure (o/slicev data n m)))

(defn -main [& args]
  (bench-take-last-while-opt))