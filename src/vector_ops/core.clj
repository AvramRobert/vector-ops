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
(defn splitv-at [at v])
(defn flattenv [v])
(defn takev-last [n v])
(defn dropv-last [n v])
(defn takev-while [pred v])
(defn dropv-while [pred v])