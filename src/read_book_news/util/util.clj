(ns read-book-news.util.util
  (:import (java.io ByteArrayInputStream))
  (:use [clojure.data.zip.xml :only (attr text xml->)])
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [net.cgrand.enlive-html :as html]
            [clojure.contrib.zip-filter.xml :as zf]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn read-url [url]
  (with-open [stream (.openStream (java.net.URL. url))]
    (let  [buf (java.io.BufferedReader. 
                (java.io.InputStreamReader. stream))]
      (apply str (line-seq buf)))))


(defn zipped [xml] 
  (zip/xml-zip xml))

(defn pull-out [val & args]
  (xml-> val args))

(defn parse-xml [content]
  "Read a URL and parse its contents with zip"
  (xml/parse content))

(defn get-value [xml & tags]
  (apply zf/xml1-> (zip/xml-zip (parse-xml xml)) (conj (vec tags) zf/text)))

(defn fetch-parsed [quote-url symbol exchange]
  "Fetches and parse HTML - use only once to minimise HTTP requests"
  (let [fetch (str quote-url symbol exchange)]
    (fetch-url fetch)))

 
(defn numeric [val]
  "Transforms a string into it's numeric equivalent"
  (cond 
   (> (.indexOf val "-") -1) 0
   (empty? val) 0
   (> (.indexOf val "%") -1) (subs val 0 (.indexOf val "%"))
   :else (Float/parseFloat val)))

(defn is-numeric? [s]
  "Check if string/sequence of chars is numeric"
  (if-let [s (seq s)]
    (let [s (if (= (first s) \-) (next s) s)
          s (drop-while #(Character/isDigit %) s)
          s (if (= (first s) \.) (next s) s)
          s (drop-while #(Character/isDigit %) s)]
      (empty? s))))
