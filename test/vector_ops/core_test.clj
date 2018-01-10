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
         (for-all [v (gen/vector gen/int)]
                  (let [i (rand-int (count v))]
                    (is (= (take i v) (takev i v))))))

(defspec dropping
         (for-all [v (gen/vector gen/int)]
                  (let [i (rand-int (count v))]
                    (is (= (drop i v) (dropv i v))))))

(defspec splitting
         (for-all [v (gen/vector gen/int)]
                  (let [i (rand-int (count v))]
                    (is (= (clojure.core/mapv vec (split-at i v)) (splitv-at i v))))))

(defspec taking-last
         (for-all [v (gen/vector gen/int)]
                  (let [i (rand-int (count v))]
                    (is (= (vec (take-last i v)) (takev-last i v))))))

(defspec dropping-last
         (for-all [v (gen/vector gen/int)]
                  (let [i (rand-int (count v))]
                    (is (= (vec (drop-last i v)) (dropv-last i v))))))

(defspec taking-while
         (for-all [v (gen/vector gen/int)]
                  (let [i (rand-int (count v))
                        p #(< % i)]
                    (is (= (vec (take-while p v)) (takev-while p v))))))

(defspec dropping-while
         (for-all [v (gen/vector gen/int)]
                  (let [i (rand-int (count v))
                        p #(< % i)]
                    (is (= (vec (drop-while p v)) (dropv-while p v))))))