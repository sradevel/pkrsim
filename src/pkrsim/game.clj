(ns pkrsim.game
  (:require [pkrsim.deck :as d]
            [pkrsim.util :as u]
            [pkrsim.handeval :as h]))

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

(defn- get-players
  [game]
  (:players game))

(defn- set-players
  [game new-players]
  (assoc game :players new-players))

(defn- fill-up-board
  [board deck]
  (u/take-into 5 board deck))

(defn- fill-up-player
  [player deck]
  (u/take-into 2 player deck))
;;
;; Play the game.
;;
(defn- draw-board-cards
  "Draws the remaining board cards from the deck."
  [game]
  (let [board (get-board game)
        deck (get-deck game)
        game-with-board (set-board game (fill-up-board board deck))]
    (set-deck game-with-board (d/remove-cards-from-deck deck (get-board game-with-board)))))

(defn- draw-player-cards
  [game]
  (let [draw-player
             (fn [r p]
               (let [hc (:holecards p)
                     d (:deck r)
                     new-p (assoc p :holecards (fill-up-player hc d))
                     new-d (d/remove-cards-from-deck d (:holecards new-p))]
                 {:deck new-d :players (conj (:players r) new-p)}))
        res (reduce draw-player {:deck (:deck game) :players []} (get-players game))]
    (assoc (assoc game :deck (:deck res)) :players (get-players res))))


(defn- draw-cards
  "Draws the remaining cards of all players and the board."
  [game]
  (let [g-board (draw-board-cards game)
        g-board-player (draw-player-cards g-board)]
    g-board-player))

(defn- evaluate-p
  "Evaluates one player"
  [board player]
  (let [v (h/find-best-hand-with-value (into board (:holecards player)))]
    (into player v)))

(defn- evaluate-players
  "Evaluates all players cards. Adds a :value key to the players vector of game."
  [game]
  (let [eval-p (partial evaluate-p (get-board game))]
    (set-players game (map eval-p (:players game)))))

(defn find-winning-hand
  "Finds the winning hand in the game. Note: the game had to be 
   played and players hands had to be evaled."
  [evaled-game]
  (let [find-max-val #(apply max (map :value (:players %)))
        players-value-p #(= (:value %) (find-max-val evaled-game))
        players #(filter players-value-p (:players %))]
    (apply h/find-best-equal-value-hand (map :hand (players evaled-game)))))

(defn- same-card-values?
  [h1 h2]
  (let [vh1 (h/sort-card-values h1)
        vh2 (h/sort-card-values h2)]
    (= vh1 vh2)))

(defn- add-result
  "Adds result as a key to the player map."
  [player result]
  (assoc player :result result))

(defn- mark-player
  [player hand]
  (if (same-card-values? (:hand player) hand)
     (add-result player 1)
     (add-result player -1)))

(defn- mark-winning-hands
  [game]
  (let [winning-hand (find-winning-hand game)]
    (set-players game (map #(mark-player % winning-hand) (:players game)))))

(defn- mark-split-pot
  "If more than one player holds the winning hand (result=1) we have a
  split pot. Therefore we need to mark the result=0"
  [game]
  (if (> (count (filter #(= (:result %) 1) (:players game))) 1)
    (set-players game (map
                       #(if (= (:result %) 1) (assoc % :result 0) %)
                       (:players game)))
    game
  ))

(defn play
  "Takes a game and plays it to the end."
  [game]
  (-> game
      draw-cards
      evaluate-players
      mark-winning-hands
      mark-split-pot)
  )

