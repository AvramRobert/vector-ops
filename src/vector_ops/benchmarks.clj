(ns vector-ops.benchmarks
  (require [vector-ops.core :as o]
           [clojure.string :as s]
           [criterium.core :as c])
  (:import (java.io StringWriter)))

(def data-ranges [0 10 100 1000 10000 100000 1000000])

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

(defn data-from [v data-type]
  (letfn [(max-min [a]
            (let [min (-> (count v) (* 0.2) (int))
                  max (-> (count v) (* 0.7) (int))]
              [(nth v min) (nth v max)]))]
    (case data-type
      :half-val (if (empty? v) 0 (nth v (-> (count v) (/ 2) (int))))
      :rand-val (if (empty? v) 0 (rand-nth v))
      :range    (cond (empty? v) [0 0]
                      (= 1 (count v)) [(first v) (first v)]
                      :else (max-min v))
      :multiset (->> (range 0 10) (mapv (constantly v)))
      v)))

(defn run-benchmark [{:keys [name ranges data-type expr]}]
  (assert (not (nil? data-type)) "Please specify the additional desired
                                  data-type to accompany the `data` in `expr`")
  (->> ranges
       (reduce
         (fn [benchmarks i]
           (let [writer (StringWriter.)
                 _ (println "Running benchmark for " name " with " i " elements.")]
             (binding [*out* writer]
               (let [vector (vec (range 0 i))
                     input  (data-from vector data-type)
                     _      (c/bench (expr vector input) :os)
                     ms     (extract-duration writer)]
                 (assoc benchmarks i ms))))) {})
       (mapv #(s/join "," %))
       (s/join "\n")
       (spit (str "./benchmarks/data/" name "-benchmark.csv"))))

(defn bench-concat-clj []
  (run-benchmark {:name "clj-concat"
                  :ranges data-ranges
                  :data-type :rand-val
                  :expr (fn [data _] (into data data))}))

(defn bench-concat-opt []
  (run-benchmark {:name "opt-concat"
                  :ranges data-ranges
                  :data-type :rand-val
                  :expr (fn [data _] (o/concatv data data))}))

(defn bench-concat-many-clj []
  (run-benchmark {:name "clj-concat-many"
                  :ranges data-ranges
                  :data-type :multiset
                  :expr (fn [_ all] (apply concat all))}))

(defn bench-concat-many-opt []
  (run-benchmark {:name "opt-concat-many"
                  :ranges data-ranges
                  :data-type :multiset
                  :expr (fn [_ all] (apply o/concatv all))}))

(defn bench-mapv-clj []
  (run-benchmark {:name "clj-map"
                  :ranges data-ranges
                  :data-type :rand-val
                  :expr (fn [data _] (mapv inc data))}))

(defn bench-mapv-opt []
  (run-benchmark {:name "opt-map"
                  :ranges data-ranges
                  :data-type :rand-val
                  :expr (fn [data _] (o/mapv inc data))}))

(defn bench-take-clj []
  (run-benchmark {:name "clj-take"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (vec (take n data)))}))

(defn bench-take-opt []
  (run-benchmark {:name "opt-take"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (o/takev n data))}))

(defn bench-drop-clj []
  (run-benchmark {:name "clj-drop"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (vec (drop n data)))}))

(defn bench-drop-opt []
  (run-benchmark {:name "opt-drop"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (o/dropv n data))}))

(defn bench-split-clj []
  (run-benchmark {:name "clj-split"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (mapv vec (split-at n data)))}))

(defn bench-split-opt []
  (run-benchmark {:name "opt-split"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (o/splitv-at n data))}))

(defn bench-take-while-clj []
  (run-benchmark {:name "clj-take-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (vec (take-while #(< % n) data)))}))

(defn bench-take-while-opt []
  (run-benchmark {:name "opt-take-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (o/takev-while #(< % n) data))}))

(defn bench-drop-while-clj []
  (run-benchmark {:name "clj-drop-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (vec (drop-while #(< % n) data)))}))

(defn bench-drop-while-opt []
  (run-benchmark {:name "opt-drop-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (o/dropv-while #(< % n) data))}))

(defn bench-take-last-while-clj [] []
  (run-benchmark {:name "clj-take-last-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (->> (reverse data) (take-while #(> % n)) (reverse)))}))

(defn bench-take-last-while-opt [] []
  (run-benchmark {:name "opt-take-last-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (o/takev-last-while #(> % n) data))}))

(defn bench-drop-last-while-clj []
  (run-benchmark {:name "clj-drop-last-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (->> (reverse data) (drop-while #(> % n)) (reverse)))}))

(defn bench-drop-last-while-opt []
  (run-benchmark {:name "opt-drop-last-while"
                  :ranges data-ranges
                  :data-type :half-val
                  :expr (fn [data n] (o/dropv-last-while #(> % n) data))}))

(defn bench-slice-clj []
  (run-benchmark {:name "clj-slice"
                  :ranges data-ranges
                  :data-type :range
                  :expr (fn [data [mn mx]] (->> data (take mx) (drop mn) (vec)))}))

(defn bench-slice-opt []
  (run-benchmark {:name "opt-slice"
                  :ranges [100 500]                                   ;data-ranges
                  :data-type :range
                  :expr (fn [data [mn mx]] (o/slicev data mn mx))}))

(defn -main [& args]
  (case (keyword (first args))
    :clj-concat (bench-concat-clj)
    :opt-concat (bench-concat-opt)
    :clj-map    (bench-mapv-clj)
    :opt-map    (bench-mapv-opt)
    :clj-take   (bench-take-clj)
    :opt-take   (bench-take-opt)
    :clj-drop   (bench-drop-clj)
    :opt-drop   (bench-drop-opt)
    :clj-take-while (bench-take-while-clj)
    :opt-take-while (bench-take-while-opt)
    :clj-drop-while (bench-drop-while-clj)
    :opt-drop-while (bench-drop-while-opt)
    :clj-take-last-while (bench-take-last-while-clj)
    :opt-take-last-while (bench-take-last-while-opt)
    :clj-drop-last-while (bench-drop-last-while-clj)
    :opt-drop-last-while (bench-drop-last-while-opt)
    :clj-split           (bench-split-clj)
    :opt-split           (bench-split-opt)
    :clj-slice           (bench-slice-clj)
    :opt-slice           (bench-slice-opt)
    :clj-concat-many     (bench-concat-many-clj)
    :opt-concat-many     (bench-concat-many-opt)
    (println "Benchmark not specified. Nothing to benchmark.")))
