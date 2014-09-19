(ns medical-ir.query-expand
  (:require [clojure.string :as str]))

(defn remove-punctuation
  [text]
  (str/replace text #"[.,]" " "))

(defn galago-query-operator
  [operator text]
  (str "#" operator "(" (remove-punctuation text) ")"))
