(ns medical-ir.relationship-util
  (:require [medical-ir.umls-db.entities :refer :all]
            [korma.core :refer :all]))

(defn get-relations
  [concept-id]
  (select mrrel
          (fields :cui2 :rel :rela)
          (where {:cui1 concept-id})
          (group :cui2 :rel :rela)
          (order :cui2)
          (order :rel)
          (order :rela)))
