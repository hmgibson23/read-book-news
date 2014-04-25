(ns read-book-news.scrapers.bloomberg
  (:use [read-book-news.util.util :only (fetch-url)])
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))

; Deliberately hard-coded for London
(def ^:dynamic *quote-url* "http://www.bloomberg.com/quote/")
(def ^:dynamic *exchange* ":LN")
(def ^:dynamic *key-stat-data* #{[:table.key_stat_data]})
(def ^:dynamic *tr-selector* [[:tr (html/has [(html/attr= :class "company_stat")])]])
(def ^:dynamic *name-selector* [[:h2 (html/attr= :itemprop "name")]])

(defn fetch-parsed [symbol]
  "Fetches and parse HTML - used only once to minimise HTTP requests"
  (let [fetch (str *quote-url* symbol *exchange*)]
    (fetch-url fetch)))


(defn numeric [val]
  "Transforms a string into it's numeric equivalent"
  (cond 
   (> (.indexOf val "-") -1) 0
   (empty? val) 0
   (> (.indexOf val "%") -1) (subs val 0 (.indexOf val "%"))
   :else (Float/parseFloat val)))

(defn get-nth [html index]
  "Get the nth item in parse html"
  (let [res (try (first (:content (nth (:content (nth html index)) 3)))
                 (catch Exception e ""))]
    (if (or (= (.indexOf res "-") -1) (empty? res))
      res
      "0")))

; Multiple lets because we only want to fetch the 
; value once to avoid spamming Bloomberg's servers
(defn package-quote [symbol]
  (let [parsed (fetch-parsed symbol)]
    (let [stat (html/select parsed *tr-selector*)
          name (first (html/select parsed *name-selector*))]
      {:symbol symbol
       :name ((fn [x] (first (:content x))) name)
       :pe (numeric (get-nth stat 0))
       :est-pe (numeric (get-nth stat 1))
       :eps (numeric (get-nth stat 3))
       :est-eps (numeric (get-nth stat 4))
       :est-peg (numeric (get-nth stat 5))
       :price-book (numeric (get-nth stat 9))
       :div-gross-yield (numeric (get-nth stat 11))
       :div-growth (numeric (get-nth stat 14))
       })))


