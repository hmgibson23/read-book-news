(ns read-book-news.commandline)

; Functions to help with command line parsing etc.

(defn show-names [collection]
  (for [item collection]
    (println (:symbol item) "\t" (:name item))))
