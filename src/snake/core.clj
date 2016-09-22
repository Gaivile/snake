(ns snake.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;; define food
(def food (atom #{}))

;; spawn food at interval 0.5 (to change to 0.025 later)
(defn spawn-food []
  (when (< (rand) 0.5)
    (swap! food conj [(rand-int 500) (rand-int 500)])))

;; remove food when eaten
(defn remove-food [meal]
  (swap! food disj meal))

;; define a snake
(def snakee (atom ()))

;; initial position for a snake
(swap! snakee conj '(250 250))
;; define a direction to move a snake
(def direction (atom []))

;; set initial value for direction
(swap! direction conj 1 0)


;; wrap a snake around the board
(defn wrap [i m]
  (loop [x i]
    (cond (< x 0) (recur (+ x m))
          (>= x m) (recur (- x m))
          :else x)))

;; move a snake
(defn move []
  (let [new-values (for [[x y] @snakee]
                    (map + [(wrap x 500) (wrap y 500)] @direction))]
    (reset! snakee new-values)))

;; change direction for the snake to move
(defn turn [state event]
  (case (:key event)
    (:w :up) (if (not= [0 1] @direction) (reset! direction [0 -1])  @direction)
    (:s :down) (if (not= [0 -1] @direction) (reset! direction [0 1])  @direction)
    (:a :left) (if (not= [1 0] @direction) (reset! direction [-1 0])  @direction)
    (:d :right) (if (not= [-1 0] @direction) (reset! direction [1 0])  @direction)
    ))


(defn setup []
  (q/smooth)
  (q/frame-rate 30)
  )

(defn update [state]
  (spawn-food)
  (move)
  )

(defn draw [state]
  (q/background 0 0 0)

  (doseq [[x y] @food]
    (q/fill 0 255 0)
    (q/stroke 0 255 0)
    (q/rect x y 5 5))

  (doseq [[x y] @snakee]
      (q/fill 255 0 0)
      (q/stroke 255 0 0)
      (q/rect x y 5 5))
  )

(q/defsketch snake
  :title "Snake"
  :size [500 500]
  :setup setup
  :update update
  :draw draw
  :key-pressed turn
  :features [:keep-on-top]
  :middleware [m/fun-mode])
