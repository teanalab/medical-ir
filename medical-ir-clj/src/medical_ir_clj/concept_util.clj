(ns medical-ir-clj.concept-util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn concept-name-here?
  [[_ lat ts _ stt _ ispref]]
  (and
   (= lat "ENG")
   (= ts "P")
   (= stt "PF")
   (= ispref "Y")))

(defn concept-ui-and-name
  [[cui _ _ _ _ _ _ _ _ _ _ _ _ _ code]]
  [cui code])

(def concept-id->concept-name
  (into {} (with-open [rdr (io/reader "../META/MRCONSO.RRF")]
             (doall (map concept-ui-and-name (filter concept-name-here? (map #(str/split % #"\|") (line-seq rdr))))))))
