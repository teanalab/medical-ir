(ns medical-ir-clj.core
  (:require [clojure.edn :as edn])
  (:gen-class))

(def config (edn/read-string (slurp "./config.clj")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
