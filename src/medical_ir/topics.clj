(ns medical-ir.topics
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.pprint :refer [print-table]]
            [medical-ir.concept-extraction :refer :all]
            [medical-ir.concept-util :refer :all]
            [medical-ir.relationship-util :refer :all]
            [medical-ir.print-util :refer :all]))

(def topics-xml (-> "topics.xml" io/resource io/file xml/parse zip/xml-zip))

(def topics
  (for [m (zip-xml/xml-> topics-xml :topic)]
    {:number (read-string (zip-xml/attr m :number))
     :type (zip-xml/attr m :type)
     :text (zip-xml/xml1-> m :summary zip-xml/text)}))

(defn extract-concepts
  [topics]
  (map #(assoc % :concepts (get-concepts-with-names (:text %))) topics))

(defn concepts-add-relationships
  [concepts]
  (for [concept concepts]
    (assoc concept :relationships
           (map #(assoc % :concept-name (concept-name (:cui2 %)))
                (get-relations (:id concept))))))

(defn extract-relations
  [topics]
  (for [topic (extract-concepts topics)
        :let [concepts {:concepts topic}]]
    (assoc topic :concepts (concepts-add-relationships (:concepts topic)))))

;;; Printing

(defn print-concept
  [{:keys [id name relationships]}]
  (apply print-in-rects (conj (concept-stys id) id name))
  (print-table [:cui2 :concept-name :rel :rela] relationships))

(defn print-topic
  [{:keys [type text concepts]}]
  (println "@@@")
  (println-in-rects type)
  (println text)
  (doseq [concept concepts]
    (print-concept concept)))

(defn print-topics
  []
  (doseq [topic (extract-relations topics)]
    (print-topic topic)))

(defn -main
  []
  (print-topics))
