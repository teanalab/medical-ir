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

(defn expand-tokens-bigrams
  [tokens operator]
  (str "#combine("
       (str/join " "
                 (map #(str operator "(" (str/join " " %) ")")
                      (partition 2 1 tokens))) ")"))

(defn expand-tokens-sequential-mrf
  [tokens]
  (str "#combine:0=0.8:1=0.1:2=0.1( " (expand-tokens-combine tokens)
       (expand-tokens-bigrams tokens "#od:1")
       (expand-tokens-bigrams tokens "#uw:8") " )"))

(defn expand-text-query
  [query expand-tokens-function]
  (-> query remove-punctuation tokenize expand-tokens-function))

(defn expand-text-sequential-mrf
  [query]
  (expand-text-query query expand-text-sequential-mrf))
