(ns medical-ir-clj.query-expand
  :use medical-ir-clj.relationship-util
  :use medical-ir-clj.concept-util)

(def expand-concepts
  [concepts]
  (map (partial expand-concept concepts)))
