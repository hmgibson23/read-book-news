(ns read-book-news.exchanges.ftse
  (:use [read-book-news.util :only (fetch-url)])
  (:require [net.cgrand.enlive-html :as html]
            [read-book-news.scrapers.bloomberg :as bloomberg]
            [clojure.string :as string]
            [read-book-news.filters :as filters]))


; Information for the FTSE exchanges. We get market data using
; one of the web scrapers

; Exchanges
(def *ftse350* 
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-350?page="
   :page-limit 4})

(def *ftse100*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-100?"
   :page-limit 1})

(def *ftse250*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-250?page="
   :page-limit 3})

(def *ftseAllShare*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-all-share?page="
   :page-limit 7})

(def *ftseSmallCap*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-small-cap?page="
   :page-limit 3})

(def *ftseAim100*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-aim-100?"
   :page-limit 1})


(def exchanges 
  {:100 *ftse100*
   :250 *ftse250*
   :350 *ftse350*
   :small *ftseSmallCap*
   :aim100 *ftseAim100*
   :all *ftseAllShare*})

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

; Using the bloomger scraper -> fetch quotes for the
; parsed results
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
; Exchange is a string which will match the exchanges
; table.
; Predicate is a function to filter the results on
; upper is the upper bound
; lower is the lower bound
(defn ftse [exchange predicate upper lower]
  (ftse-fetch ((keyword exchange) exchanges))
  (doseq [fut @futures] @fut)
  (predicate upper lower @results))
