(ns medical-ir-clj.core
  (:require [clojure.edn :as edn]))

(def config (edn/read-string (slurp "./config.clj")))
