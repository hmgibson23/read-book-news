(ns read-book-news.stocks
  (:require [net.cgrand.enlive-html :as html]))

(def track-urls {:goog "http://google.com/finance?q="})
(def *sharebox-selector*
               #{[:div#sharebox-data]})


(defn get-price [symbol]
  (html/select (html/html-resource 
                (java.net.URL. (:goog track-urls)))
               *sharebox-selector*))
