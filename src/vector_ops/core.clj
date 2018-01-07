(ns vector-ops.core)

(defn concatv
  ([] [])
  ([a] a)
  ([a b] (hamt.RT/concat a b))
  ([a b c] (hamt.RT/concat a b c))
  ([a b c d] (hamt.RT/concat a b c d))
  ([a b c d e] (hamt.RT/concat a b c d e))
  ([a b c d e & rest] (hamt.RT/concatMany (cons a (cons b (cons c (cons d (cons e rest))))))))

(defn mapv [f v] (hamt.RT/map v f))

(defn takev [n v] (hamt.RT/take v n))

(defn dropv [n v] (hamt.RT/drop v n))

(defn splitv-at [at v] [(takev at v) (dropv at v)])

(defn takev-last [n v] (hamt.RT/takeLast v n))
(defn dropv-last [n v] (hamt.RT/dropLast v n))
(defn takev-while [pred v] (hamt.RT/takeWhile v pred))
(defn dropv-while [pred v] (hamt.RT/dropWhile v pred))

(defn flattenv [v] (vec (flatten v)))                       ;; good enough
