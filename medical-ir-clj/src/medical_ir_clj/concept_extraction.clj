(ns medical-ir-clj.concept-extraction
  (:require [metamap-api.metamap-api :as mm]
            [medical-ir-clj.concept-util :refer :all]))

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

(defn get-conceptid
  [utterance-list]
  (map :conceptid (apply concat (get-ev-list utterance-list))))

(defn get-concept-ids
  [text]
  (let [[{utterance-list :utterance-list}] (process-string text)]
    (flatten (map get-conceptid utterance-list))))

(defn get-concepts-with-names
  [text]
  (map #(hash-map :concept-id % :concept-name (concept-name %))
       (get-concept-ids text)))
