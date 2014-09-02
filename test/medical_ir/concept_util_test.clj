(ns medical-ir.concept-util-test
  (:require [clojure.test :refer :all]
            [medical-ir.concept-util :refer :all]))

(deftest getting-concept-name
  (testing "Simple medical term"
    (is (= "Influenza" (concept-name "C0021400")))))
