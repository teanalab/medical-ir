(ns medical-ir.umls-db.db
  (:require [medical-ir.core :refer [config]]
            [korma.db :refer :all]
            [korma.config :refer [set-naming]]
            [clojure.string :as string]))

(defdb db (mysql (:database config)))

(set-naming {:keys string/lower-case :fields string/upper-case})
