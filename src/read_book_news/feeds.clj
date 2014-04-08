(ns read-book-news.feeds
  (:use [clojure.data.zip.xml :only (attr text xml->)])
  (require [read-book-news.util :as util]
           [read-book-news.parse :as parse]
           ))

(def urls {
           :new-yorker "http://www.newyorker.com/online/blogs/books/rss.xml"
           :guardian "http://www.theguardian.com/books/rss"
           })


(defn new-yorker []
  (for [x  (:entries 
            (parse/parse-feed "http://www.newyorker.com/online/blogs/books/rss.xml"))] 
    (println (:title x))))
