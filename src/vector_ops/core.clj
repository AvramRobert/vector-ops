(ns vector-ops.core)

(defn concatv
  ([] [])
  ([a] a)
  ([a b] (hamt.RT/concat a b))
  ([a b c] (hamt.RT/concat a b c))
  ([a b c d] (hamt.RT/concat a b c d))
  ([a b c d e] (hamt.RT/concat a b c d e))
  ([a b c d e & rest] (hamt.RT/concatMany (cons a (cons b (cons c (cond d (cons e rest))))))))