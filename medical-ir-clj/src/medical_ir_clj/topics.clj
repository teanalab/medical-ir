(ns medical-ir-clj.topics
  (:require [clojure.java.io :as io]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [medical-ir-clj.concept-extraction :refer :all]
            [medical-ir-clj.relationship-util :refer :all]))

(def topics-xml (-> "topics.xml" io/resource io/file xml/parse zip/xml-zip))

(def topics
  (for [m (zip-xml/xml-> topics-xml :topic)]
    {:type (zip-xml/attr m :type) :text (zip-xml/xml1-> m :summary zip-xml/text)}))

(defn extract-concepts
  [topics]
  (map #(assoc % :concepts (get-concepts-with-names (:text %))) topics))

(defn concepts-add-relations
  [concepts]
  (for [concept concepts]
    (assoc concept :relations (map #(assoc % :concept-name (concept-name (:cui2 %)))(get-relations (:concept-id concept))))))

(defn extract-relations
  [topics]
  (for [topic (extract-concepts topics)
        :let [concepts {:concepts topic}]]
    (assoc topic :concepts (concepts-add-relations (:concepts topic)))))
