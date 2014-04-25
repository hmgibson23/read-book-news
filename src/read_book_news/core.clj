(ns read-book-news.core
  (require [read-book-news.feeds :as feeds]
           [read-book-news.cli.commandline :as commandline])
  (:gen-class))

(defn -main
  "Read some books new from the internet"
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))  
  (commandline/ftse-selection))

