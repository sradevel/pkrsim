(ns pkrsim.game
  (:require [pkrsim.deck :refer :all]
            [pkrsim.util :refer :all]))

;;
;; access methods
;;

(defn- get-deck
  [game]
  (:deck game))

(defn- get-board
  [game]
  (:board game))

(defn- set-board
  [game new-board]
  (assoc game :board new-board))

(defn- set-deck
  [game new-deck]
  (assoc game :deck new-deck))

(defn- fill-up-board
  [board deck]
  (take-into 5 board deck))

(defn- fill-up-player
  [player deck]
  (take-into 2 player deck))
;;
;; Play the game.
;;
(defn- draw-board-cards
  "Draws the remaining board cards from the deck."
  [game]
  (let [board (get-board game)
        deck (get-deck game)
        game-with-board (set-board game (fill-up-board board deck))]
    (set-deck game-with-board (remove-cards-from-deck deck (get-board game-with-board)))))

(defn- draw-player-cards
  [game]
  (let [draw-player
             (fn [r p]
               (let [hc (:holecards p)
                     d (:deck r)
                     new-p (assoc p :holecards (fill-up-player hc d))
                     new-d (remove-cards-from-deck d (:holecards new-p))]
                 {:deck new-d :players (conj (:players r) new-p)}))
        res (reduce draw-player {:deck (:deck game) :players []} (:players game))]
    (assoc (assoc game :deck (:deck res)) :players (:players res))))


(defn- draw-cards
  "Draws the remaining cards of all players and the board."
  [game]
  (let [g-board (draw-board-cards game)
        g-board-player (draw-player-cards g-board)]
    g-board-player))

(defn play
  "Takes a game and plays it to the end."
  [game]
  (let [game-played (draw-cards game)]
    game-played))
