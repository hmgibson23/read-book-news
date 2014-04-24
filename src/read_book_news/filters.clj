(ns read-book-news.filters)

; Some filters for parsing our stocks
(defn p-e [upper lower coll]
  (filter (fn [x] (and (< (:pe x) upper) (> (:pe x) lower))) coll))
