(ns read-book-news.exchanges.stocks
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def ^:dynamic *ftse350* "http://www.hl.co.uk/shares/stock-market-summary/ftse-350?page=")

; selectors - used to get the relevant info from above urls
(def ^:dynamic *odd-selector* #{[:tr.table-odd html/first-child]})
(def ^:dynamic *alt-selector* #{[:tr.table-alt html/first-child]})

(defn parse-results-350 [res selector]
  "Specifically for our FTSE 350 analysis"
  (let [parsed (html/select res selector)]
    (for [item parsed
          :when (not= (:tag item) :a)]
      (:content item))))

                                        ; with these we can get all their prices
(defn ftse-350 []
  "Get the symbols of every stock in the FTSE 350"
  (flatten (for [x [1 2 3 4]]
             (let [raw-html (fetch-url (str *ftse350* x))]
               (let [res-one (future (parse-results-350 raw-html *odd-selector*))
                     res-two (future (parse-results-350 raw-html *alt-selector*))]
                 (concat @res-one @res-two))))))
