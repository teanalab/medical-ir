(ns medical-ir-clj.query-expand
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [opennlp.nlp :refer :all]))

(defn remove-punctuation
  [text]
  (str/replace text #"[.,]" " "))

(def tokenize (-> "en-token.bin" io/resource io/file str make-tokenizer))

(defn expand-tokens-combine
  [tokens]
  (str "#combine(" (str/join " " tokens) ")"))

(defn expand-tokens-sequential-mrf
  [tokens]
  (str "#sdm(" (str/join " " tokens) ")"))

(defn expand-text-query
  [query expand-tokens-function]
  (-> query remove-punctuation tokenize expand-tokens-function))

(defn expand-text-combine
  [query]
  (expand-text-query query expand-tokens-combine))

(defn expand-text-sequential-mrf
  [query]
  (expand-text-query query expand-tokens-sequential-mrf))
