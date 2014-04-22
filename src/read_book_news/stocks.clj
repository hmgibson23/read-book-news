(ns read-book-news.stocks
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))


(def ^:dynamic *goog* "http://google.com/finance?q=")
(def ^:dynamic *sharebox-selector* #{[:div#sharebox-data]})
(def ^:dynamic *price-meta* [[:meta (html/attr= :itemprop "price")]])
(def ^:dynamic *sharetable-selector* #{[:table.snap-data]})


(defn get-price [symbol]
  (:content (:attrs (first (html/select (fetch-url (str *goog* symbol))
                                        *price-meta*)))))
