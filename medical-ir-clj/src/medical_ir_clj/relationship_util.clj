(ns medical-ir-clj.relationship-util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]))

(defn ro-relationship-here?
  [[_ _ _ rel _ _ _ rela]]
  (and
   (= rel "RO")
   ((complement str/blank?) rela)))

(defn concepts-and-relationship
  [[cui1 _ _ _ cui2 _ _ rela]]
  [cui1 cui2 rela])

(def relationships
  (with-open [rdr (io/reader "../META/MRREL.RRF")]
    (doall (map concepts-and-relationship
                (filter ro-relationship-here?
                        (map #(str/split % #"\|")
                             (take 500 (line-seq rdr))))))))

(def concepts->relationship
  (into {} (map (fn [[cui1 cui2 rela]] [[cui1 cui2] rela]) relationships)))

(def concept-relationship->concepts
  (apply merge-with
         (partial merge-with set/union)
         (map (fn [[cui1 cui2 rela]] {cui1 {rela #{cui2}}}) relationships)))
