(ns read-book-news.util
  (:use [clojure.data.zip.xml :only (attr text xml->)])
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.contrib.zip-filter.xml :as zf]))

(defn read-url [url]
  (slurp url))


(defn zipped [xml] 
  (zip/xml-zip xml))

(defn pull-out [val & args]
  (xml-> val args))

(defn parse-xml [content]
  "Read a URL and parse its contents with zip"
  (xml/parse content))


(defn get-struct-map [xml]
  (if-not (empty? xml)
    (let [stream (ByteArrayInputStream. (.getBytes (.trim xml)))]
      (xml/parse stream))))
 
(defn get-value [xml & tags]
  (apply zf/xml1-> (zip/xml-zip (get-struct-map xml)) (conj (vec tags) zf/text)))



