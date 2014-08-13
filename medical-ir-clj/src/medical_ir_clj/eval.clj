(ns medical-ir-clj.eval
  (:require [clojure.data.json :as json]
            [medical-ir-clj.topics :refer :all]
            [medical-ir-clj.query-expand :refer :all]))

(def eval-topics (filter #(<= (:number %) 10) topics))

(defn eval-topics-expanded-json
  [query-expansion-function]
  {:queries (map #(hash-map :number (str (:number %))
                            :text (query-expansion-function (:text %)))
                 eval-topics)})

(def queries-json-file (java.io.File/createTempFile "queries" ".json"))

(spit queries-json-file
      (json/write-str (eval-topics-expanded-json expand-text-combine)))
