(ns snake.core
  (:import [java.awt.event KeyEvent])
  (:require [quil.core :as q]
            [quil.core :refer :all]
            [quil.middleware :as m]
            [quil.core :as q :include-macros true]))


; add two vectors
; (v+ [1 2] [1 1]) => [2 3]
;(def v+ (partial mapv +[1 2] [1 1]))

; given a percent chance, decide if a event happens
; (food-interval 0.5) => true
; (food-interval 0.5) => false
(defn food-interval [n]
  (< (rand) n))

(food-interval 0.025)

; food (as set of points)
(def food (atom #{}))

; add a meal at random position
(swap! food conj [(rand-int 400) (rand-int 400)])

; snake (as vector of points because we want to append)
; => obviously needs a start position
;(def snake (atom [[50 50]]))

; snake movement direction
;(def snake-dir (atom [1 0]))

; update
(defn update []
  ; spawn food
  (when (food-interval 0.025)
    (swap! food conj [(rand-int 400) (rand-int 400)])))
   ;;update snake
 ;; (let [new (v+ (first @snake) @snake-dir)] ; new head
   ;; (swap! snake #(vec (cons new (pop %)))))) ; head+rest


; draw
(defn draw []
  ; set background color to dark gray
  (q/background 0x20)
  ; draw food
  (q/stroke 0x00 0xff 0xff)
  (doseq [[x y] @food]
    (q/fill 0x00 0xff 0xff)
    (q/rect x y 5 5))
  ;; draw snake
  ;;(q/fill 255)
  ;;(doseq [[x y] @snake] (q/rect x y 5 5))
  )


; input
(defn key-pressed []
  (cond ; case doesn't work with the KeyEvents
    (= (q/key-code) KeyEvent/VK_UP)
      (reset! snake-dir [0 -1])
    (= (q/key-code) KeyEvent/VK_DOWN)
      (reset! snake-dir [0 1])
    (= (q/key-code) KeyEvent/VK_LEFT)
      (reset! snake-dir [-1 0])
    (= (q/key-code) KeyEvent/VK_RIGHT)
      (reset! snake-dir [1 0])))

; let the snake eat food
#_(add-watch snake :key
  (fn [k r os ns]
    ; any meal under snake head?
    (let [meal (get @food (first ns))]
      (when meal
        (swap! food disj meal))))) ; remove from food
        ;(swap! snake #(vec (cons meal %))))))) ; add to snake

; run
(q/defsketch snake
  :title "Snaaaaaaake!"
  :size [400 400]
  :setup (fn [] (q/smooth) (q/no-stroke) (q/frame-rate 60))
  :draw (fn [] (update) (draw))
  :key-pressed key-pressed)
