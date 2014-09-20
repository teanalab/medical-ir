(ns medical-ir.concept-extraction
  (:require [metamap-api.metamap-api :as mm]
            [medical-ir.concept-util :refer :all]))

(def mmapi (mm/api-instantiate))

(def process-string #(mm/handle-result-list (mm/process-string mmapi % "-y")))

(defn get-phrase-mappings-list
  [utterance-list]
  (:phrase-mappings-list utterance-list))

(defn get-mappings
  [utterance-list]
  (map :mappings (get-phrase-mappings-list utterance-list)))

(defn get-ev-list
  [utterance-list]
  (map :ev-list (apply concat (get-mappings utterance-list))))

(defn get-concept
  [utterance-list text]
  (map #(hash-map :id (:conceptid %)
                  :name (:conceptname %)
                  :preferredname (:preferredname %)
                  :text (subs text (-> % :position first :start)
                              (+ (-> % :position first :start)
                                 (-> % :position first :length)))
                  :words (:matchedwords %)
                  :semtypes (:semtypes %))
       (apply concat (get-ev-list utterance-list))))

(defn get-concepts-with-names
  [text]
  (if (clojure.string/blank? text)
    nil
    (let [[{utterance-list :utterance-list}] (process-string text)]
     (flatten (map #(get-concept % text) utterance-list)))))