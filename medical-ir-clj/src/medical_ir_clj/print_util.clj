(ns medical-ir-clj.print-util)

(defn print-in-rects
  [& args]
  (letfn [(print-horizontal-bound []
            (print \+)
            (doseq [arglen (map count args)]
              (print (apply str (repeat arglen \-)))
              (print \+))
            (println))
          (print-args []
            (print \|)
            (doseq [arg args]
              (print arg)
              (print \|))
            (println))]
    (print-horizontal-bound)
    (print-args)
    (print-horizontal-bound)))
