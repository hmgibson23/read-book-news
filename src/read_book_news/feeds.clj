(ns read-book-news.feeds
  (:use [clojure.data.zip.xml :only (attr text xml->)])
  (require [read-book-news.util :as util]))

(def urls { 
           :new-yorker "http://www.newyorker.com/online/blogs/books/rss.xml" 
           })

(defn new-yorker []
  (for [x (get-in
           (util/parse-xml (urls :new-yorker))
           [:content])]
    (for [x  (:content x)]
      (println x)
      )
    ))   
