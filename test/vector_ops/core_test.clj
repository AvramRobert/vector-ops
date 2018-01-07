(ns vector-ops.core-test
  (:require [vector-ops.core :refer :all]
            [clojure.test :refer [is]]
            [clojure.test.check.properties :refer [for-all]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]])
  (:import (hamt Util)))


(defspec concatenation
         (for-all [v1 (gen/vector gen/int)
                   v2 (gen/vector gen/int)]
                  (is (= (concatv v1 v2) (into v1 v2)))))


(defspec mapping
         (for-all [v (gen/vector gen/int)]
                  (is (= (clojure.core/mapv inc v) (mapv inc v)))))


(defspec taking
         (for-all [v (gen/vector gen/int)
                   i gen/int]
                  (is (= (take i v) (takev i v)))))

(defspec dropping
         (for-all [v (gen/vector gen/int)
                   i gen/int]
                  (is (= (drop i v) (dropv i v)))))