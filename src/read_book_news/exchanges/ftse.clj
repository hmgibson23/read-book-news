(ns read-book-news.exchanges.ftse
  (:use [read-book-news.util :only (fetch-url)])
  (:require [net.cgrand.enlive-html :as html]
            [read-book-news.scrapers.bloomberg :as bloomberg]
            [clojure.string :as string]
            [read-book-news.filters :as filters]))


; Information for the FTSE exchanges. We get market data using
; one of the web scrapers



(def *ftse350* 
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-350?page="
   :page-limit 4})

(def ^:dynamic *odd-selector* #{[:tr.table-odd html/first-child]})
(def ^:dynamic *alt-selector* #{[:tr.table-alt html/first-child]})

(defn parse-results [res selector]
  "Helps get symbols from the HL website"
  (let [parsed (html/select res selector)]
    (for [item parsed
          :when (not= (:tag item) :a)]
      (:content item))))




; Shared atoms for pushing results
(def results (atom []))
(def futures (atom []))

(defn get-quotes [sequence]
  "In a separate thread, get the quotes for passed sequence"
  (future ((fn [x] 
             (doseq [symb x]
               (swap! results conj 
                      (bloomberg/package-quote (first symb))))) 
           sequence)))

; with these we can get all their prices
; all the futures are put into a pool
; which is then waited on in the low-pe function
(defn ftse-fetch [exchange]
  "Get the symbols of every stock in the FTSE 350"
  (flatten (for [x (range (:page-limit exchange))]
             (let [raw-html (fetch-url (str (:url exchange) x))]
               (let [res-one (future (parse-results raw-html *odd-selector*))
                     res-two (future (parse-results raw-html *alt-selector*))]
                 (swap! futures conj (get-quotes (concat @res-one @res-two))))))))


; We delay and wait for all the futures
; before - then we apply the passed predicate
(defn ftse [exchange predicate upper lower]
  (ftse-fetch exchange)
  (doseq [fut @futures] @fut)
  (predicate upper lower @results))
