(ns medical-ir-clj.eval
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [medical-ir-clj.core :refer :all]
            [medical-ir-clj.topics :refer [topics]]
            [medical-ir-clj.query-expand :refer :all])
  (:import java.io.PrintStream
           org.lemurproject.galago.core.tools.App))

(def default-expansion-function expand-text-sequential-mrf)

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

(defn queries-json-file
  "make queries file with required filename"
  [filename]
  (spit filename (queries-json default-expansion-function)))

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

(defn batch-search-file
  [filename]
  (batch-search default-expansion-function (PrintStream. filename)))

(defn evaluate
  [query-expansion-function judgements-filepath]
  (let [batch-search-results (batch-search-tmp-file query-expansion-function)]
    (App/main (into-array ["eval"
                           (str "--judgments=" judgements-filepath)
                           (str "--baseline=" batch-search-results)]))))


(defn -main
  [& args]
  (println "qrelsLDA.txt: ")
  (evaluate default-expansion-function (-> "qrelsLDA.txt" io/resource io/file str))
  (println)
  (println "qrelsDS.txt: ")
  (evaluate default-expansion-function (-> "qrelsDS.txt" io/resource io/file str)))
