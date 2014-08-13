(ns medical-ir-clj.query-expand)


(defn expand-text-combine
  [query]
  (str "#combine(" (clojure.string/replace query #"\." "") ")"))
