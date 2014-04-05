(ns read-book-news.core
  (:gen-class))


(defn -main
  "Read some books new from the internet"
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))
