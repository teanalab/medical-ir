(ns medical-ir-clj.documents
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
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

(defn cut-text
  [body]
  (if body
    (str/join " " (take 100 (str/split body #"\s+")))
    nil))

(defn extract-summary-and-body
  [nxml-file]
  (let [root (-> nxml-file (xml/parse startparse-sax) zip/xml-zip)]
    {:abstract (cut-text (zip-xml/xml1-> root :front :article-meta :abstract zip-xml/text))
     :body (cut-text (zip-xml/xml1-> root :body zip-xml/text))}))

(defn extract-text
  [nxml-file]
  (let [{:keys [abstract body]} (extract-summary-and-body nxml-file)]
    (or abstract body)))

(defn -main
  []
  (doseq [nxml-file nxml-files]
    (print (str
            "<DOC> <DOCNO>"
            (str/replace-first (.getName nxml-file) #".nxml" "")
            "</DOCNO> <TEXT> "))
    (flush)
    (println (str (str/join \  (get-concept-ids (extract-text nxml-file)))
                  "</TEXT> </DOC>"))))
