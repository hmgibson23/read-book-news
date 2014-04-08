(ns read-book-news.core
  (require [read-book-news.feeds :as feeds])
  (:gen-class))

(def look-up { 
              :a feeds/new-yorker
              :b feeds/guardian
              })


(defn get-selection []
  (println "Read some books news: 
            Choices:
            A) The New Yorker
            B) The Guardian") 
  (read-line))


(defn -main
  "Read some books new from the internet"
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))
