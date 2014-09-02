(ns medical-ir-clj.umls-db.db
  (:require [medical-ir-clj.core :refer [config]]
            [korma.db :refer :all]
            [korma.config :refer [set-naming]]
            [clojure.string :as string]))

(defdb db (mysql (:database config)))

(set-naming {:keys string/lower-case :fields string/upper-case})
