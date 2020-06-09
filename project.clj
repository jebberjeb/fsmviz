(defproject fsmviz "0.1.4"
  :description "Generate Graphviz diagrams from FSM data."
  :url "https://github.com/jebberjeb/fsmviz"
  :license {:name "MIT"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha17" :scope "provided"]
                 [org.clojure/clojurescript "1.9.854" :scope "provided"]
                 [specviz "0.2.4"]]

  :jar-exclusions [#"out/compiled/.*"]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :source-paths ["src"]

  :cljsbuild
  {:builds
   [{:id "release"
     :source-paths ["src"]
     :compiler {:optimizations :advanced
                :output-to "resources/out/fsmviz.js"
                :asset-path "out"
                :output-dir "resources/out/compiled"
                :main "fsmviz.core"}}]})
