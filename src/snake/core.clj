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
(def snakee (atom {}))

;; initial position for a snake
(swap! snakee conj [250 250])

;; move a snake
(defn move [velocity]
  (swap! snakee conj (into [] (map + (first @snakee) velocity)))
  (if (not= (first velocity) 0)
    (swap! snakee dissoc (first (map key @snakee)))
    ))


(defn setup []
  (q/smooth)
  (q/frame-rate 30)
  )

(defn update [state]
  (spawn-food)
  (move [0 -1])
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
  :features [:keep-on-top]
  :middleware [m/fun-mode])
