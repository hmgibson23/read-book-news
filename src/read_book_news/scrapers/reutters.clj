(ns read-book-news.scrapers.reutters
  (:require [read-book-news.util.util :refer (fetch-url fetch-parsed numeric is-numeric?)]
            [clojure.string :refer (trim)]
            [net.cgrand.enlive-html :as html]))

; Get information from Reutters
; only grab key financials from them

(def financial-highlights-url 
  "http://www.reuters.com/finance/stocks/financialHighlights?symbol=")
(def financial-statement-url 
  "http://www.reuters.com/finance/stocks/incomeStatement?symbol=")
(def exchange ".L")
(def financials-selector  [[:div (html/has [(html/attr= :class "moduleHeader")])]])
(def table-selector  #{[:table.dataTable]})



; Parsing data from Reutters web page is a pretty involved process
(defn create-pairs [item]
  (let [title (trim (:content (second (:content item))))
        content (:content (nth (:content item) 3))]
    {:title title :content content}))

(defn check-value [item]
  (cond (not (map? item)) false
        :else (contains? item :tag)))

(defn prepare-content [html]
  "Build a list of (header, value) pairs for easy searching."
  (let [search (:content (second (:content html)))]
    (doall (map create-pairs (filter (fn [x] (> (count (:content x)) 3)) 
                                     (filter check-value search))))))


(defn categorise-divs [html]
  "Return a Map of neatly categorised divs, based on their title
   i.e moduleHeader"
  (let [title (second html)]
    (println html)))

(defn pair-create [html]
  (flatten (doall (map prepare-content html))))

(defn financial-statement [symbol]
  (let [parsed (html/select (fetch-parsed financial-highlights-url symbol exchange) 
                            table-selector)]
    (pair-create parsed)))
