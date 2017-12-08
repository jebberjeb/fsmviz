(ns demo-cljs.core
    (:require [fsmviz.core :as fsm]))

(enable-console-print!)

(println "loading")

;; Works
(set! (.-innerHTML (.getElementById js/document "fsm1"))
      (fsm/generate-image [[:start :foo :bar]] "fsm1.svg"))

(set! (.-innerHTML (.getElementById js/document "fsm2"))
      (fsm/generate-image {:start {:foo :bar}} "fsm2.svg"))
