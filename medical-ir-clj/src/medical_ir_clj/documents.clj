(ns medical-ir-clj.documents
  "Functions to export corpus in trectext format, optionally extracting
  concepts. Before running this on TREC 2014 collection you should delete
  pmc-text-01/11/2760706.nxml file because it has invalid xml (or you can just
  correct it)."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip :as zf]
            [clojure.data.zip.xml :as zip-xml]
            [medical-ir-clj.core :refer :all]
            [medical-ir-clj.concept-extraction :refer [get-concept-ids]])
  (:import (javax.xml.parsers SAXParser SAXParserFactory)))

(defn startparse-sax
  "Don't validate the DTDs, they are usually messed up."
  [s ch]
  (let [factory (SAXParserFactory/newInstance)]
    (.setFeature factory "http://apache.org/xml/features/nonvalidating/load-external-dtd" false)
    (let [^SAXParser parser (.newSAXParser factory)]
      (.parse parser s ch))))

(def nxml-files (filter (complement (memfn isDirectory))
                        (-> (:source-collection-path config)
                            clojure.java.io/file
                            file-seq)))

(defn text
  "Modified version of clojure.data.zip.xml/text. Inserts spaces between tags
  content."
  [loc]
  (.replaceAll
   ^String (str/join " " (zip-xml/xml-> loc zf/descendants zip/node string?))
   (str "[\\s" (char 160) "]+") " "))

(defn extract-summary-and-body
  [nxml-file]
  (let [root (-> nxml-file (xml/parse startparse-sax) zip/xml-zip)]
    {:abstract (zip-xml/xml1-> root :front :article-meta :abstract text)
     :body (zip-xml/xml1-> root :body text)}))

(defn clean-text
  "Get rid of non-ASCII characters"
  [text]
  (if (string? text)
    (clojure.string/replace text #"[^\u0000-\u007F]" "")
    text))

(defn extract-text
  [nxml-file]
  (let [{:keys [abstract body]} (extract-summary-and-body nxml-file)]
    (str abstract " " body)))

(defn extract-concept-ids
  [nxml-file]
  (->> nxml-file
       extract-text
       clean-text
       get-concept-ids
       (str/join \ )))

(defn extract-pmcid
  [nxml-file]
  (str/replace-first (.getName nxml-file) #".nxml" ""))

(defn trectext-str
  [nxml-file text-function]
  (str
   "<DOC>\n"
   "<DOCNO>" (extract-pmcid nxml-file) "</DOCNO>\n"
   "<TEXT>\n"
   (text-function nxml-file) "\n"
   "</TEXT>\n"
   "</DOC>"))

(defn trectext-fulltext
  []
  (doseq [nxml-file nxml-files]
    (println (trectext-str nxml-file extract-text))))


(defn trectext-concepts
  []
  (doseq [nxml-file nxml-files]
    (println (trectext-str nxml-file extract-concept-ids))))

(defn metamap-batch
  []
  (doseq [nxml-file nxml-files]
    (println (str (extract-pmcid nxml-file)
                  "|"
                  (-> nxml-file extract-text clean-text)))))
