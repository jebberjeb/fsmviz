(defproject fsmviz "0.1.3"
  :description "Generate Graphviz diagrams from FSM data."
  :url "https://github.com/jebberjeb/fsmviz"
  :license {:name "MIT"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854"]
                 [specviz "0.2.4"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :source-paths ["src"]

  :cljsbuild
  {:builds
   [{:id "release"
     :source-paths ["src"]
     :compiler {:optimizations :advanced
                :output-to "resources/out/fsmviz.js"
                :asset-path "out"
                :output-dir "resources/out"
                :main "fsmviz.core"}}]})
