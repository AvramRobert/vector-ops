(ns vector-ops.core-test
  (:require [vector-ops.core :refer :all]
            [clojure.test :refer [is]]
            [clojure.test.check.properties :refer [for-all]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]))


(defspec concatenation
         (for-all [v1 (gen/vector gen/int)
                   v2 (gen/vector gen/int)]
                  (is (= (concatv v1 v2) (into v1 v2)))
                  (is (= v1 v1))
                  (is (= v2 v2))))