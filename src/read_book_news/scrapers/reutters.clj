(ns read-book-news.scrapers.reutters)

; Get information from Reutters
; only grab key financials from them

(def financial-highlights "http://www.reuters.com/finance/stocks/financialHighlights?symbol=")
(def financial-statement "http://www.reuters.com/finance/stocks/incomeStatement?symbol=")
