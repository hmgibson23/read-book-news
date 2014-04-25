(ns read-book-news.util.filters)

; Some filters for parsing our stocks

(defn coll-filter [key upper lower coll]
  (let [k (keyword key)]
    (filter (fn [x] (and (< (k x) upper) (> (k x) lower))) coll)))


; Macro to create a filter function based on its name
(defmacro create-filter [filter-name]
  `(defn ~(symbol filter-name) [~'upper ~'lower ~'coll]
     (coll-filter ~(name filter-name) ~'upper ~'lower ~'coll)))


; Our filters 
(create-filter "pe")         
(create-filter "est-pe") 
(create-filter "eps")
(create-filter "est-eps")
(create-filter "est-peg")
(create-filter "price-book")
(create-filter "div-gross-yield")
(create-filter "div-growth")



