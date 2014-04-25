(ns read-book-news.exchanges.ftse
  (:use [read-book-news.util.util :only (fetch-url)])
  (:require [net.cgrand.enlive-html :as html]
            [read-book-news.scrapers.bloomberg :as bloomberg]
            [clojure.string :as string]
            [read-book-news.util.filters :as filters]))


; Information for the FTSE exchanges. We get market data using
; one of the web scrapers

; Exchanges
(def ^:dynamic *ftse350* 
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-350?page="
   :page-limit 4})

(def ^:dynamic *ftse100*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-100?"
   :page-limit 1})

(def ^:dynamic *ftse250*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-250?page="
   :page-limit 3})

(def ^:dynamic *ftseAllShare*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-all-share?page="
   :page-limit 7})

(def ^:dynamic *ftseSmallCap*
  {:url "http://www.hl.co.uk/shares/stock-market-summary/ftse-small-cap?page="
   :page-limit 3})

(def ^:dynamic *ftseAim100*
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

; Using the bloomberg scraper -> fetch quotes for the
; parsed results
; DEPRECAED IN FAVOUR OF A MUCH FASTER VERSION BELOW
(defn get-quotes [sequence results]
  "In a separate thread, get the quotes for passed sequence"
  (future ((fn [x] 
             (doseq [symb x]
               (swap! results conj 
                      (bloomberg/package-quote (first symb))))) 
           sequence)))


; An alternative way of processing
; Spawn a thread for each symbol and crunch through them
(defn fetch-all [sequence results]
  (doall (map
          (fn [symb]
            (future 
              (bloomberg/package-quote (first symb))))
          sequence)))

; Use a future to collect all of the other futures 
; that have run for the quotes
(defn get-quotes-all [sequence results]
  (future 
    (let [x (fetch-all sequence results)]
      (doseq [y x]
        (swap! results conj @y)))))
           

; with these we can get all their prices
; all the futures are put into a pool
; which is then waited on in the low-pe function
(defn ftse-fetch [exchange results futures]
  "Get the symbols of every stock in the passed exchange"
  (flatten (for [x (range (:page-limit exchange))]
             (let [raw-html (fetch-url (str (:url exchange) x))]
               (let [res-one (future (parse-results raw-html *odd-selector*))
                     res-two (future (parse-results raw-html *alt-selector*))]
                 (swap! futures conj (get-quotes-all (concat @res-one @res-two) results)))))))


; We delay and wait for all the futures
; before - then we apply the passed predicate
; Exchange is a string which will match the exchanges
; table.
; Predicate is a function to filter the results on
; upper is the upper bound
; lower is the lower bound
(defn ftse [exchange predicate upper lower]
  (let [results (atom [])
        futures (atom [])]
    (ftse-fetch ((keyword exchange) exchanges) results futures)
    (doseq [fut @futures] @fut)
    (predicate upper lower @results)))
