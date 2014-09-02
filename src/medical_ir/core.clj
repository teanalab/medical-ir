(ns medical-ir.core
  (:require [clojure.edn :as edn]))

(def config (edn/read-string (slurp "./config.clj")))
