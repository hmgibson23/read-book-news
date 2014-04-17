(ns read-book-news.core
  (require [read-book-news.feeds :as feeds])
  (:gen-class))

(def look-up { 
              :a (feeds/new-yorker)
              :b (feeds/guardian)
              })

(defn parse-selection [x]
  (look-up (keyword x)))

(defn get-selection []
  (println "Read some books news: 
            Choices:
            A) The New Yorker
            B) The Guardian") 
  (parse-selection (read-line)))


(defn -main
  "Read some books new from the internet"
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  
  (println "Welcome! Get some book news!")
  (get-selection))

