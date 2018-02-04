(ns vector-ops.benchmarks.markdown
  (require [clojure.string :as s]
           [vector-ops.benchmarks.bench :as b]))

(def output-file b/output-file)
(defn ops-f [f] (str "(vector-ops.core/" f ")"))
(defn clj-f [f] (str "(vec (" f "))"))

(defn csv->map [csv]
  (->> (s/split-lines csv)
       (mapv #(s/split % #","))
       (into {})))

(defn entry [{:keys [title clj-code opt-code note clj-csv opt-csv]}]
  (letfn [(row [[k [clj opt]]] (str "| " k " | " clj " | " opt " |"))]
    (str "### " title "\n"
         "**Note:** " (or note "-") "\n\n"
         "**Clojure:** `" clj-code "`\n\n"
         "**Optimised:** `" opt-code "`\n\n"
         "| **Size** | **Clojure**   | **Optimised**   | \n"
         "| -------- | :----------------: | :------------------: | \n"
         (->> (csv->map opt-csv)
              (merge-with vector (csv->map clj-csv))
              (mapv row)
              (s/join "\n")))))


(def benchmark-data
  (into (sorted-map)
        {:concat          (fn []
                            {:title    "Concatenation"
                             :clj-code (clj-f "concat v1 v2")
                             :opt-code (ops-f "concatv v1 v2")
                             :clj-csv  (slurp (output-file "clj-concat"))
                             :opt-csv  (slurp (output-file "opt-concat"))})

         :concat-many     (fn []
                            {:title    "Batch concatenation"
                             :note     "Concatenated 10 vectors of given `size` at once."
                             :clj-code (clj-f "apply concat vs")
                             :opt-code "(apply vector-ops.core/concatv vs)"
                             :clj-csv  (slurp (output-file "clj-concat-many"))
                             :opt-csv  (slurp (output-file "opt-concat-many"))})

         :map             (fn []
                            {:title    "Mapping"
                             :clj-code "(mapv f vs)"
                             :opt-code (ops-f "mapv f vs")
                             :clj-csv  (slurp (output-file "clj-map"))
                             :opt-csv  (slurp (output-file "opt-map"))})

         :take            (fn []
                            {:title    "Taking"
                             :clj-code (clj-f "take n v")
                             :opt-code (ops-f "takev n v")
                             :clj-csv  (slurp (output-file "clj-take"))
                             :opt-csv  (slurp (output-file "opt-take"))})

         :drop            (fn []
                            {:title    "Dropping"
                             :clj-code (clj-f "drop n v")
                             :opt-code (ops-f "dropv n v")
                             :clj-csv  (slurp (output-file "clj-drop"))
                             :opt-csv  (slurp (output-file "opt-drop"))})

         :take-last       (fn []
                            {:title    "Taking last"
                             :clj-code (clj-f "take-last n v")
                             :opt-code (ops-f "takev-last n v")
                             :clj-csv  (slurp (output-file "clj-take-last"))
                             :opt-csv  (slurp (output-file "opt-take-last"))})

         :drop-last       (fn []
                            {:title    "Dropping last"
                             :clj-code (clj-f "drop-last n v")
                             :opt-code (ops-f "dropv-last n v")
                             :clj-csv  (slurp (output-file "clj-drop-last"))
                             :opt-csv  (slurp (output-file "opt-drop-last"))})

         :take-while      (fn []
                            {:title    "Taking while"
                             :clj-code (clj-f "take-while n v")
                             :opt-code (ops-f "takev-while n v")
                             :clj-csv  (slurp (output-file "clj-take-while"))
                             :opt-csv  (slurp (output-file "opt-take-while"))})

         :drop-while      (fn []
                            {:title    "Dropping while"
                             :clj-code (clj-f "drop-while n v")
                             :opt-code (ops-f "dropv-while n v")
                             :clj-csv  (slurp (output-file "clj-drop-while"))
                             :opt-csv  (slurp (output-file "opt-drop-while"))})

         :take-last-while (fn []
                            {:title    "Taking last while"
                             :clj-code (clj-f "->> (reverse v) (take-while p) (reverse)")
                             :opt-code (ops-f "takev-last-while n v")
                             :clj-csv  (slurp (output-file "clj-take-last-while"))
                             :opt-csv  (slurp (output-file "opt-take-last-while"))})

         :drop-last-while (fn []
                            {:title    "Dropping last while"
                             :clj-code (clj-f "->> (reverse v) (drop-while p) (reverse)")
                             :opt-code (ops-f "dropv-last-while n v")
                             :clj-csv  (slurp (output-file "clj-drop-last-while"))
                             :opt-csv  (slurp (output-file "opt-drop-last-while"))})

         :split-at        (fn []
                            {:title    "Splitting at"
                             :clj-code "(mapv vec (split-at n v))"
                             :opt-code (ops-f "splitv-at n v")
                             :clj-csv  (slurp (output-file "clj-split"))
                             :opt-csv  (slurp (output-file "opt-split"))})

         :slice           (fn []
                            {:title    "Slicing"
                             :note     "Like `clojure.core/subvec`, but returns a proper vector, not a subvector"
                             :clj-code (clj-f "->> v (take m) (drop n)")
                             :opt-code (ops-f "slicev v n m")
                             :clj-csv  (slurp (output-file "clj-slice"))
                             :opt-csv  (slurp (output-file "opt-slice"))})}))

(defn page []
  (str
    "# Benchmarks
  These benchmarks have been done using `criterium`. You may find the code in `vector_ops.benchmarks.bench`. \n
  As with any benchmarks, these don't project a perfect view of reality and I don't claim they do as such. 
  They solely deem as an indicator of the degree of improvement these optimisations might have over the Clojure alternatives. \n
  
  Benchmarks have been run on: \n
  **Operating System**: Ubuntu 16.04 (64 bit) \n
  **CPU**: Intel i7-6700HQ CPU (6MB cache, 2.60 - 3.50 GHz x 4 cores) \n
  **Memory**: 16 GB DDR4 \n"
    (->> benchmark-data
         (mapv (fn [[_ gen-data]] (entry (gen-data))))
         (s/join "\n"))))
