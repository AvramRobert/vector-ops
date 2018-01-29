(ns vector-ops.core)

(defn concatv
  "`clojure.core/concat` but optimised for vectors. Returns a vector"
  ([] [])
  ([a] a)
  ([a b] (hamt.RT/concat a b))
  ([a b c] (hamt.RT/concat a b c))
  ([a b c d] (hamt.RT/concat a b c d))
  ([a b c d & rest] (hamt.RT/concatMany (cons a (cons b (cons c (cons d rest)))))))

(defn mapv
  "`clojure.core/mapv` but slightly more optimised for vectors. Returns a vector"
  [f v] (hamt.RT/map v f))

(defn takev
  "`clojure.core/take` but optimised for vectors. Returns a vector"
  [n v] (hamt.RT/take v n))

(defn dropv
  "`clojure.core/drop` but optimised for vectors. Returns a vector"
  [n v] (hamt.RT/drop v n))

(defn splitv-at
  "`clojure.core/split-at` but optimised for vectors. Returns a vector of vectors"
  [at v] [(takev at v) (dropv at v)])

(defn slicev [v from until]
  "Similar to `clojure.core/subvec` but returns a proper vector in O(m) time, where m = until - from.
   Its clojure equivalent is: (->> v (take until) (drop from) (vec))"
  (hamt.RT/slice v from until))

(defn takev-last
  "`clojure.core/take-last` but optimised for vectors. Returns a vector"
  [n v] (dropv (- (count v) n) v))

(defn dropv-last
  "`clojure.core/drop-last` but optimised for vectors. Returns a vector"
  [n v] (takev (- (count v) n) v))

(defn takev-while
  "`clojure.core/take-while` but optimised for vectors. Returns a vector"
  [pred v] (hamt.RT/takeWhile v pred))

(defn dropv-while
  "`clojure.core/drop-while` but optimised for vectors. Returns a vector"
  [pred v] (hamt.RT/dropWhile v pred))

(defn takev-last-while
  "Takes elements from the end of a vector as long as `pred` yields a truthy value.
   Returns a vector. The order of the original vector is kept.
   Its clojure equivalent is: (->> v (reverse) (take-while pred) (reverse) (vec))"
  [pred v] (hamt.RT/takeLastWhile v pred))

(defn dropv-last-while
  "Drops elements from the end of a vector as long as `pred` yields a truthy value.
   Returns a vector. The order of the original vector is kept:
   Its clojure equivalent is: (->> v (reverse) (drop-while pred) (reverse) (vec))"
  [pred v] (hamt.RT/dropLastWhile v pred))

(defn reversev
  "`clojure.core/reverse` but coerces the result to a vector"
  [v] (vec (reverse v)))                       ;; good enough

(defn flattenv
  "`clojure.core/flatten` but coerces the result to a vector"
  [v] (vec (flatten v)))                       ;; good enough