(ns read-book-news.filters)

; Some filters for parsing our stocks


(defn coll-filter [key upper lower coll]
  (let [k (keyword key)]
    (filter (fn [x] (and (< (k x) upper) (> (k x) lower))) coll)))

(defn p-e [upper lower coll]
  (coll-filter "pe" upper lower coll))

(defn peg [upper lower coll]
  (coll-filter "peg" upper lower coll))

