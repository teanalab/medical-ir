(ns medical-ir-clj.concept-util-test
  (:require [clojure.test :refer :all]
            [medical-ir-clj.concept-util :refer :all]))

(deftest getting-concept-name
  (testing "Simple medical term"
    (is (= "Influenza" (concept-name "C0021400")))))
