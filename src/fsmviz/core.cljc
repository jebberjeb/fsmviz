(ns fsmviz.core
  (:require
    [clojure.set :as set]
    [clojure.spec.alpha :as s]
    [specviz.graphviz :as graphviz]))

(s/def ::fsm-tuple (s/tuple any? any? any?))
(s/def ::fsm-tuples (s/coll-of ::fsm-tuple))
(s/def ::fsm-transition-map (s/nilable (s/map-of any? any?)))
(s/def ::fsm-map (s/map-of any? ::fsm-transition-map))
(s/def ::fsm (s/or :tuples ::fsm-tuples
                   :map ::fsm-map))

(defn- map->tuples
  "Returns a collection of [from via to] tuples representing the FSM."
  [state-map]
  (mapcat (fn [[from m]]
            (map (fn [[trans to]]
                   [from trans to])
                    m))
          state-map))

(defn- third [coll] (nth coll 2))

(defn- start-states
  "Returns states which have no inbound transitions."
  [tuples]
  (set/difference (set (map first tuples)) (set (map third tuples))))

(defn- term-states
  "Returns states which have no outbound transitions."
  [tuples]
  (set/difference (set (map third tuples)) (set (map first tuples))))

(defn clean-name
  "Sanitize a name, per `graphviz/name`, and if the state's name is nil,
  assume it is a start state."
  [state]
  (if state (graphviz/clean-name state) "start"))

(defn tuples->graphviz
  [tuples]
  (concat
    (mapcat (fn [[from via to]]
              [{::graphviz/from (clean-name from)
                ::graphviz/label via
                ::graphviz/to (clean-name to)}])
            tuples)

    ;; Style initial states
    [{::graphviz/name "start"
      ::graphviz/label ""
      ::graphviz/height 0.25
      ::graphviz/width 0.25
      ::graphviz/shape "circle"
      ::graphviz/style "filled"
      ::graphviz/fillcolor "#000000"}]

    ;; Style terminal states
    (mapv (fn [state]
            {::graphviz/name (clean-name state)
             ::graphviz/shape "doublecircle"})
          (term-states tuples))) )

(defmulti fsm->graphviz* first)

(defmethod fsm->graphviz* :tuples
  [[_ fsm-tuples]]
  (tuples->graphviz fsm-tuples))

(defmethod fsm->graphviz* :map
  [[_ fsm-map]]
  (-> fsm-map
      map->tuples
      tuples->graphviz))

(defn fsm->graphviz
  "Returns a collection of Graphviz elements representing the `fsm`."
  [fsm]
  (let [conformed (s/conform ::fsm fsm)]
    (if (= conformed :clojure.spec/invalid)
      (do (println (s/explain ::fsm fsm))
          [{::graphviz/name "Error"}])
      (fsm->graphviz* conformed))))

(s/fdef generate-image :args (s/cat :state-data ::fsm
                                    :filename string?))

;; TODO this won't handle cursors
#?(:cljs
   (defn transform-js-data
     [state-data]
     (mapv (fn [row]
             (mapv (fn [x] (if (string? x)
                             (keyword x)
                             x))
                   row))
           (js->clj state-data))))

(defn ^:export generate-image
  "Creates <filename>.svg, using the state map provided.

  `state-data` a map of state -> transition map, or a colletion of
               [from via to] triples."
  [state-data filename]
  (-> state-data
      #?(:cljs transform-js-data)
      fsm->graphviz
      graphviz/dot-string
      (graphviz/generate-image! filename)))

(comment
  (fsmviz.core/generate-image {:start {:t1 :foo
                                       :t2 :bar}
                               :foo {:t3 :baz}
                               :bar {:t4 :baz}} "example-map")
  (fsmviz.core/generate-image [[:start :t1 :x]
                               [:start :t2 :y]
                               [:start :t3 :z]
                               [:z :t4 :x]
                               [:z :t5 :finish]
                               [:x :t6 :finish]
                               [:y :t7 :finish]] "example-tuples"))
