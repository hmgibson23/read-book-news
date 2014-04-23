(ns read-book-news.fr
  (:require [clojure.string :as string]))

(defn radar-string []
  (let [result (slurp "http://www.footballradar.com/quiz/")]
    (subs result
          (+ (.indexOf result "{") 1)
          (.indexOf result "}"))))


(def ^:dynamic *test* "21 + 25 * 74")

(defn get-int [vec]
  (Integer. vec))

(defn at-index [vec index]
  (get-int (get vec index)))

(defn get-mult-val [vec]
  (let [mult (.indexOf vec "*")]
    (cond 
     (> mult -1) (* (at-index vec (- mult 1)) (at-index vec (+ mult 1))))))

(defn calc-string [string]
  (let [vec (string/split string #" ")]
    (println vec)
    (get-mult-val vec)))
    
