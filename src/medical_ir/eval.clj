(ns medical-ir.eval
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [medical-ir.core :refer :all]
            [medical-ir.topics :refer [topics]]
            [medical-ir.query-expand :refer :all]
            [medical-ir.print-util :refer :all])
  (:import java.io.PrintStream
           org.lemurproject.galago.core.tools.App))

(def default-expansion-function (partial galago-query-operator "wsdm"))

(def eval-topics (filter #(<= (:number %) 10) topics))

(defn eval-topics-expanded-json
  [query-expansion-function]
  {:queries (map #(hash-map :number (str (:number %))
                            :text (query-expansion-function (:text %)))
                 eval-topics)})

(defn queries-json
  "constructs json string for queries"
  [query-expansion-function]
  (json/write-str (eval-topics-expanded-json query-expansion-function)))

(defn queries-json-tmp-file
  "create temprorary file with queries and return it"
  [query-expansion-function]
  (let [queries-json-file (java.io.File/createTempFile "queries" ".json")]
    (spit queries-json-file (queries-json query-expansion-function))
    queries-json-file))

(defn queries-json-print
  "make queries file with required filename"
  [& args]
  (println (queries-json default-expansion-function)))

(defn batch-search
  [query-expansion-function print-stream]
  (let [queries-json-file (queries-json-tmp-file query-expansion-function)]
    (App/run (into-array ["batch-search"
                          (str "--index=" (:galago-index-path config))
                          (str queries-json-file)])
      print-stream)
    (.delete queries-json-file)))

(defn batch-search-tmp-file
  [query-expansion-function]
  (let [search-results (java.io.File/createTempFile "search-results" ".json")]
    (batch-search query-expansion-function (PrintStream. search-results))
    search-results))

(defn batch-search-print
  []
  (batch-search default-expansion-function System/out))

(defn evaluate
  [query-expansion-function judgements-filepath]
  (let [batch-search-results (batch-search-tmp-file query-expansion-function)]
    (App/main (into-array ["eval"
                           (str "--judgments=" judgements-filepath)
                           (str "--baseline=" batch-search-results)]))))


(defn print-metrics-for-expansion
  [query-expansion-function]
  (println "qrelsLDA.txt:")
  (evaluate query-expansion-function
            (-> "qrelsLDA.txt" io/resource io/file str))
  (println)
  (println "qrelsDS.txt:")
  (evaluate query-expansion-function
            (-> "qrelsDS.txt" io/resource io/file str)))

(def galago-operators ["combine" "sdm" "fulldep" "wsdm"])

(defn -main
  [& args]
  (doseq [galago-operator galago-operators]
    (println-in-rects galago-operator)
    (print-metrics-for-expansion
     (partial galago-query-operator galago-operator))
    (println)))
