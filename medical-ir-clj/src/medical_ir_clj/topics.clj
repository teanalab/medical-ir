(ns medical-ir-clj.topics
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.pprint :refer [print-table]]
            [medical-ir-clj.concept-extraction :refer :all]
            [medical-ir-clj.concept-util :refer :all]
            [medical-ir-clj.relationship-util :refer :all]))

(def topics-xml (-> "topics.xml" io/resource io/file xml/parse zip/xml-zip))

(def topics
  (for [m (zip-xml/xml-> topics-xml :topic)]
    {:type (zip-xml/attr m :type)
     :text (zip-xml/xml1-> m :summary zip-xml/text)}))

(defn extract-concepts
  [topics]
  (map #(assoc % :concepts (get-concepts-with-names (:text %))) topics))

(defn concepts-add-relationships
  [concepts]
  (for [concept concepts]
    (assoc concept :relationships
           (map #(assoc % :concept-name (concept-name (:cui2 %)))
                                   (get-relations (:concept-id concept))))))

(defn extract-relations
  [topics]
  (for [topic (extract-concepts topics)
        :let [concepts {:concepts topic}]]
    (assoc topic :concepts (concepts-add-relationships (:concepts topic)))))

;;; Printing

(def indent-str "  ")
(defn print-indent
  [indent-level]
  (print (apply str (repeat indent-level indent-str))))

(defn print-relationship
  [{:keys [concept-name rel rela]} indent-level]
  (print-indent indent-level)
  (println (str concept-name "\tREL: " rel "\tRELA: " rela)))

(defn print-concept
  [{:keys [concept-name relationships]} indent-level]
  (print-indent indent-level)
  (println concept-name)
  (doseq [relationship relationships]
    (print-relationship relationship (inc indent-level))))

(defn print-topic
  [{:keys [type text concepts]} indent-level]
  (println (str "Type: " type))
  (println (str "Text: " text))
  (doseq [concept concepts]
    (print-concept concept (inc indent-level))))

(defn print-topics
  []
  (doseq [topic (extract-relations topics)]
    (print-topic topic 0)))
