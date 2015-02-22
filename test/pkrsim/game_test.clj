(ns pkrsim.game-test
    (:require [expectations :refer :all]
              [pkrsim.game :refer :all]))

;; Pull in Private methods
(def get-deck          #'pkrsim.game/get-deck)
(def set-deck          #'pkrsim.game/set-deck)
(def get-board         #'pkrsim.game/get-board)
(def set-board         #'pkrsim.game/set-board)
(def draw-board-cards  #'pkrsim.game/draw-board-cards)
(def draw-player-cards #'pkrsim.game/draw-player-cards)
(def draw-cards        #'pkrsim.game/draw-cards)

;; Test games
(def test-deck-1 ["KH" "TS" "4D" "7C" "AC"])

(def unfinished-game-without-deck {:deck []
        :board ["AH" "2D" "3C"]
        :players [{:id 1 :holecards ["TD"]}
                  {:id 2 :holecards []}]})

(def unfinished-game-with-deck {:deck test-deck-1
        :board ["AH" "2D" "3C"]
        :players [{:id 1 :holecards ["TD"]}
                  {:id 2 :holecards []}]})

(def unfinished-game-with-drawn-board {:deck ["4D" "7C" "AC"]
        :board ["AH" "2D" "3C" "KH" "TS"]
        :players [{:id 1 :holecards ["TD"]}
                  {:id 2 :holecards []}]})

(def unfinished-game-with-drawn-players {:deck ["7C" "AC"]
        :board ["AH" "2D" "3C"]
        :players [{:id 1 :holecards ["TD" "KH"]}
                  {:id 2 :holecards ["TS" "4D"]}]})

(def unfinished-game-with-drawn-cards {:deck []
        :board ["AH" "2D" "3C" "KH" "TS"]
        :players [{:id 1 :holecards ["TD" "4D"]}
                  {:id 2 :holecards ["7C" "AC"]}]})

;;
;; getters and setters
;;
(expect unfinished-game-with-deck
        (set-deck unfinished-game-without-deck test-deck-1))
(expect test-deck-1 (get-deck unfinished-game-with-deck))
(expect {:board []} (set-board {} []))
(expect ["AH" "2D" "3C"] (get-board unfinished-game-with-deck))

;;
;; draw cards
;;
(expect unfinished-game-with-drawn-board
        (draw-board-cards unfinished-game-with-deck))
(expect unfinished-game-with-drawn-players
        (draw-player-cards unfinished-game-with-deck))
(expect unfinished-game-with-drawn-cards
        (draw-cards unfinished-game-with-deck))
