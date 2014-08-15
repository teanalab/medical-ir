(ns medical-ir-clj.concept-extraction-test
  (:require [clojure.test :refer :all]
            [medical-ir-clj.concept-extraction :refer :all]))

(deftest concept-extraction
  (testing "Simple medical term"
    (is (= '("C0242379") (get-concept-ids "lung cancer")))))
