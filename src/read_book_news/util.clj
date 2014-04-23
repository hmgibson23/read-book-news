(ns read-book-news.util
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



