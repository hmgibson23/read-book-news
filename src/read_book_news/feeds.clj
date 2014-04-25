(ns read-book-news.feeds
  (:use [clojure.data.zip.xml :only (attr text xml->)])
  (require [read-book-news.util.util :as util]
           [read-book-news.parse :as parse]
           ))

(def urls {
           :new-yorker "http://www.newyorker.com/online/blogs/books/rss.xml"
           :guardian "http://www.theguardian.com/books/rss"
           })


(defn new-yorker []
  (println "The New Yorker")
  (for [x  (:entries 
            (parse/parse-feed (:new-yorker urls)))] 
    (println (:title x))))

(defn guardian []
  (println "The Guardian Books")
  (for [x (:entries
           (parse/parse-feed (:guardian urls)))]
    (println (:title x))))
