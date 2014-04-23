(ns read-book-news.scrapers.goog
  (:use [read-book-news.util :only (fetch-url)])
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))


; This file explicitly contains functionality to 
; to scrape Google Finance for stock related 
; information. Others to come
(def ^:dynamic *goog* "http://google.com/finance?q=")
(def ^:dynamic *sharebox-selector* #{[:div#sharebox-data]})
(def ^:dynamic *price-meta* [[:meta (html/attr= :itemprop "price")]])
(def ^:dynamic *name-meta* [[:meta (html/attr= :itemprop "name")]])
(def ^:dynamic *pe-meta* [[:tr (html/has [(html/attr= :data-snapfield "pe_ratio")])]])
(def ^:dynamic *eps-meta* [[:tr (html/has [(html/attr= :data-snapfield "eps")])]])
(def ^:dynamic *quotetime-selector* [[:meta (html/attr= :itemprop "quoteTime")]])
(def ^:dynamic *sharetable-selector* #{[:table.snap-data]})

; helper
(defn get-elements [url selector]
  (first (html/select (fetch-url url) selector)))

(defn get-sharebox-data [url selector]
  (:content (:attrs (get-elements url selector))))
; get the time of this quote
(defn get-quotetime [symbol]
  (get-sharebox-data (str *goog* symbol) *quotetime-selector*))

; get the current price
(defn get-price [symbol]
  (get-sharebox-data (str *goog* symbol) *price-meta*))

; get the name
(defn get-name [symbol]
  (get-sharebox-data (str *goog* symbol) *name-meta*))

; helps to get nested meta info
(defn get-meta [sequence]
  (if sequence
    (string/trim-newline (nth (:content (nth (:content sequence) 3)) 0))
    ""))


; Get the metascope value - these are generic
(defn get-meta-val [symbol selector]
  (get-meta ((fn [x y] (get-elements (str *goog* x) y)) symbol selector)))

; get the EPS from a given symbol
(defn get-eps [symbol]
  (get-meta-val symbol *eps-meta*))

; get the P/E ratio
(defn get-pe [symbol]
  (get-meta-val symbol *pe-meta*))

; create a map, containing all the relevant information
; about the passed symbol
(defn package-quote [symbol]
  {:symbol 'symbol
   :name (get-name symbol)
   :price (get-price symbol)
   :eps (get-eps symbol)
   :pe (get-pe symbol)
   :quote-time (get-quotetime symbol)})


