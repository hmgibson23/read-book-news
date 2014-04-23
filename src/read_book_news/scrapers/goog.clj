(ns read-book-news.scrapers.goog
  (:use [read-book-news.stocks :only (fetch-url)])
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))



; This file explicityl contains functionality to 
; to scrape Google Finance for stock related 
; information. Others to come

(def ^:dynamic *goog* "http://google.com/finance?q=")
(def ^:dynamic *sharebox-selector* #{[:div#sharebox-data]})
(def ^:dynamic *price-meta* [[:meta (html/attr= :itemprop "price")]])

; selects the parent
(def ^:dynamic *eps-meta* [[:tr (html/has [(html/attr= :data-snapfield "eps")])]])

(def ^:dynamic *sharetable-selector* #{[:table.snap-data]})



(defn get-elements [html selector]
  (first (html/select html selector)))

; uses Google Finance to get the current price
(defn get-price [symbol]
  (:content (:attrs (get-elements
                     (fetch-url (str *goog* symbol))
                     *price-meta*))))


; get the EPS from a given symbol
(defn get-eps [symbol]
  (string/trim-newline (nth (:content
                             (nth (:content
                                   ((fn [x]
                                      (get-elements 
                                       (fetch-url 
                                        (str *goog* x)) *eps-meta*)) symbol))
                                  3)) 0)))


(defn get-quotetime [symbol])
(defn get-pe [symbol])
(defn get-last-div-yield [symbol])