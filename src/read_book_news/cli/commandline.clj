(ns read-book-news.cli.commandline
  (require [read-book-news.exchanges.ftse :as ftse]
           [read-book-news.util.filters :as filters]))

; Functions to help with command line parsing etc.
(defn show-names [collection]
  (doseq [item collection]
    (println (:symbol item) "\t" (:name item))))


(def look-up-ftse
  {:a "100"
   :b "250"
   :c "350"
   :d "small"
   :e "aim100"
   :f "all"})


(def filter-table
  {:a filters/pe
   :b filters/est-pe
   :c filters/est-peg
   :d filters/eps
   :e filters/est-eps
   :f filters/price-book
   :g filters/div-gross-yield
   :h filters/div-growth})



(defn read-upper []
  (println "Upper limit: ")
  (Integer. (read-line)))

(defn read-lower []
  (println "Lower limit: ")
  (Integer. (read-line)))

(defn choose-limits [exchange predicate]
  (let [upper (read-upper)
        lower (read-lower)]
    (println "Fetching companies matching criteria...")
    (show-names (ftse/ftse exchange predicate upper lower))))

(defn choose-filter [exchange]
  (println "\nChoose a filter:
              A) P/E
              B) Est. P/E
              C) PEG
              D) EPS
              E) Est. EPS
              F) Price/Book
              G) Div. Gross Yield
              H) Div. Growth")
  (let [sel ((keyword (read-line)) filter-table)]
    (choose-limits exchange sel)))


(defn ftse-selection []
  (println "Welcome to Stock Recommender")
  (println "Choose an exchange:
            A) FTSE 100
            B) FTSE 250
            C) FTSE 350
            D) FTSE AIM 100
            E) FTSE SMALL CAP
            F  FTSE ALL SHARE")
  (let [sel ((keyword (read-line)) look-up-ftse)]
    (choose-filter sel)))
