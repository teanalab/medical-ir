(ns medical-ir-clj.query-expand
  (:use medical-ir-clj.relationship-util
        medical-ir-clj.concept-util))

(defn expand-concepts
  [concepts]
  (map (partial expand-concept concepts)))
