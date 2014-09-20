(ns medical-ir.concept-extraction-test
  (:require [clojure.test :refer :all]
            [medical-ir.concept-extraction :refer :all]))

(deftest concept-extraction
  (testing "Simple medical term"
    (is (= '("C0242379") (map :id (get-concepts-with-names "lung cancer"))))))
