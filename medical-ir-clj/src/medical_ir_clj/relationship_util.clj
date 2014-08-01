(ns medical-ir-clj.relationship-util
  (:require [medical-ir-clj.umls-db.entities :refer :all]
            [korma.core :refer :all]))

(defn get-relations
  [concept-id]
  (select mrrel
          (fields :cui2 :rel :rela)
          (where {:cui1 concept-id})))
