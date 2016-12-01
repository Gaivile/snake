(ns snake.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;;define world
(def world { :width 100 :height 100})

;; define food
(def food (atom #{}))

(defn spawn-food
  "Spawn food at interval 0.5"
  []
  (when (< (rand) 0.5)
    (swap! food conj [(rand-int (world :width)) (rand-int (world :width))])))

;; define a snake
(def snakee (atom []))

(defn remove-food
  "Remove food when eaten"
  []
  (swap! food disj (first @snakee)))

;; initial position for a snake
(swap! snakee conj [50 50])

;; define a direction to move a snake
(def direction (atom []))

;; set initial value for direction
(swap! direction conj 1 0)

(defn wrap
  "Wrap a snake around the board"
  [i m]
  (loop [x i]
    (cond (< x 0) (recur (+ x m))
          (>= x m) (recur (- x m))
          :else x)))

(defn turn
  "Change direction for the snake to move"
  [state event]
  (case (:key event)
    (:w :up) (if (not= [0 1] @direction) (reset! direction [0 -1])  @direction)
    (:s :down) (if (not= [0 -1] @direction) (reset! direction [0 1])  @direction)
    (:a :left) (if (not= [1 0] @direction) (reset! direction [-1 0])  @direction)
    (:d :right) (if (not= [-1 0] @direction) (reset! direction [1 0])  @direction)))

(defn reset
  "Reset a snake if it touches itself"
  []
  (if (apply distinct? @snakee)
    @snakee
    (reset! snakee (into [] (take 1 @snakee)))))

(defn score
  "Show score"
  []
  (str "Score: " (count @snakee)))

;; colour to paint the food
(def colour (atom '(255 13 169)))

(defn paint-food
  "Create a fancy colour for the snake"
  []
  (let [x (rand-int 255)
        y (rand-int 255)
        z (rand-int 255)]
    (reset! colour (into '() [x y z]))))

(defn move
  "Move a snake"
  []
  (let [new-values (into []
       (map + [(wrap (first (first @snakee)) (world :width)) (wrap (second (first @snakee)) (world :height))] @direction))]
    (if (contains? @food (first @snakee))
      (do
        (remove-food)
        (paint-food)
        (swap! snakee conj new-values))
      (reset! snakee (drop-last 1 (conj @snakee new-values))))))

(defn setup []
  (q/smooth)
  (q/frame-rate 15))

(defn update [state]
  (spawn-food)
  (move)
  (reset)
  (score))

(defn draw [state]
  (let [w (/ (q/width) (world :width))
        h (/ (q/height) (world :height))]
    (q/background 0 0 0)

    (doseq [[x y] @food]
      (q/fill (first @colour) (second @colour) (last @colour))
      (q/stroke (first @colour) (second @colour) (last @colour))
      (q/rect (* w x) (* h y) w h))

    (doseq [[x y] @snakee]
        (q/fill (rand-int 255) (rand-int 255) (rand-int 255))
        (q/stroke (rand-int 255) (rand-int 255) (rand-int 255))
        (q/rect (* w x) (* h y) w h)))

  (q/fill 100 255 100)
  (q/text-size 20)
  (q/text (score) 10 30 ))

(q/defsketch snake
  :title "Snake"
  :size [700 700]
  :setup setup
  :update update
  :draw draw
  :key-pressed turn
  :features [:keep-on-top]
  :middleware [m/fun-mode])
