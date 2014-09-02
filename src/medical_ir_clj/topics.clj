(ns medical-ir-clj.topics
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.pprint :refer [print-table]]
            [medical-ir-clj.concept-extraction :refer :all]
            [medical-ir-clj.concept-util :refer :all]
            [medical-ir-clj.relationship-util :refer :all]
            [medical-ir-clj.print-util :refer :all]))

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
                (get-relations (:concept-id concept))))))

(defn extract-relations
  [topics]
  (for [topic (extract-concepts topics)
        :let [concepts {:concepts topic}]]
    (assoc topic :concepts (concepts-add-relationships (:concepts topic)))))

;;; Printing

(defn print-concept
  [{:keys [concept-id concept-name relationships]}]
  (apply print-in-rects (conj (concept-stys concept-id) concept-id concept-name))
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
