(ns pkrsim.game-test
    (:require [expectations :refer :all]
              [pkrsim.game :refer :all]))

;; Pull in Private methods
(def get-deck           #'pkrsim.game/get-deck)
(def set-deck           #'pkrsim.game/set-deck)
(def get-board          #'pkrsim.game/get-board)
(def set-board          #'pkrsim.game/set-board)
(def draw-board-cards   #'pkrsim.game/draw-board-cards)
(def draw-player-cards  #'pkrsim.game/draw-player-cards)
(def draw-cards         #'pkrsim.game/draw-cards)
(def evaluate-players   #'pkrsim.game/evaluate-players)
(def f-w-h              #'pkrsim.game/find-winning-hand)
(def m-w-h              #'pkrsim.game/mark-winning-hands)
(def m-s-p              #'pkrsim.game/mark-split-pot)

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

(def unfinished-game-with-pairs {:deck []
        :board ["TS" "AS" "3C" "2C" "JS"]
        :players [{:id 1 :holecards ["AC" "AD"]}
                  {:id 2 :holecards ["KD" "KC"]}]})

(def unfinished-game-with-split-pot {:deck []
        :board ["TS" "AS" "3C" "QC" "JS"]
        :players [{:id 1 :holecards ["AC" "2D"]}
                  {:id 2 :holecards ["AD" "2C"]}]})

(def complex-game {:deck []
                   :board ["JD" "TD" "QC" "2D" "3C"]
                   :players [{:id 1 :holecards ["AS" "KS"]} ;; Straight
                             {:id 2 :holecards ["QD" "4D"]} ;; Flush
                             {:id 3 :holecards ["AD" "KD"]} ;; Flush
                             {:id 4 :holecards ["2C" "2C"]}]}) ;; Set

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

;;
;; evaluate-player
;;

;; helper methods
(defn get-value-from-player-id
  [players id]
  (first (filter #(= (:id %) id) players)))

(defn get-player-value-from-game
  [game id]
  (:value (get-value-from-player-id (:players game) id)))

;; both players hold an Pair (one Pair of Aces, one Pair of Tens)
(def ev-game1 (evaluate-players unfinished-game-with-drawn-cards))
(expect 1 (get-player-value-from-game ev-game1 1))
(expect 1 (get-player-value-from-game ev-game1 2))

;; One player should have a set, one a pair
(def ev-game2 (evaluate-players unfinished-game-with-pairs))
(expect 3 (get-player-value-from-game ev-game2 1))
(expect 1 (get-player-value-from-game ev-game2 2))

;; Both players hold a Pair of Aces
(def ev-game3 (evaluate-players unfinished-game-with-split-pot))
(expect 1 (get-player-value-from-game ev-game3 1))
(expect 1 (get-player-value-from-game ev-game3 2))

(def ev-cgame (evaluate-players complex-game))
(expect 4 (get-player-value-from-game ev-cgame 1))
(expect 5 (get-player-value-from-game ev-cgame 2))
(expect 5 (get-player-value-from-game ev-cgame 3))
(expect 3 (get-player-value-from-game ev-cgame 4))

;;
;; find-winning-hand
;; 
(def whand1 (sort ["AC" "AS" "AD" "TS" "JS"]))
(def whand2 (sort ["AH" "AC" "KH" "TS" "7C"]))
(def whand3 (sort ["AD" "KD" "JD" "2D" "TD"]))
(expect whand1 (sort (f-w-h ev-game2)))
(expect whand2 (sort (f-w-h ev-game1)))
(expect whand3 (sort (f-w-h ev-cgame)))

;;
;; mark-winning-hands
;;
(defn get-player-result-from-game
  [game id]
  (:result (get-value-from-player-id (:players game) id)))

(expect -1 (get-player-result-from-game (m-w-h ev-game1) 1))
(expect 1 (get-player-result-from-game (m-w-h ev-game1) 2))

(expect 1 (get-player-result-from-game (m-w-h ev-game2) 1))
(expect -1 (get-player-result-from-game (m-w-h ev-game2) 2))

(expect 1 (get-player-result-from-game (m-w-h ev-game3) 1))
(expect 1 (get-player-result-from-game (m-w-h ev-game3) 2))

(expect -1 (get-player-result-from-game (m-w-h ev-cgame) 1))
(expect -1 (get-player-result-from-game (m-w-h ev-cgame) 2))
(expect 1 (get-player-result-from-game (m-w-h ev-cgame) 3))
(expect -1 (get-player-result-from-game (m-w-h ev-cgame) 4))

;;
;; mark-split-pot
;;
(expect -1 (get-player-result-from-game (m-s-p (m-w-h ev-game1)) 1))
(expect 1 (get-player-result-from-game (m-s-p (m-w-h ev-game1)) 2))

(expect 1 (get-player-result-from-game (m-s-p (m-w-h ev-game2)) 1))
(expect -1 (get-player-result-from-game (m-s-p (m-w-h ev-game2)) 2))

(expect 0 (get-player-result-from-game (m-s-p (m-w-h ev-game3)) 1))
(expect 0 (get-player-result-from-game (m-s-p (m-w-h ev-game3)) 2))

(expect -1 (get-player-result-from-game (m-s-p (m-w-h ev-cgame)) 1))
(expect -1 (get-player-result-from-game (m-s-p (m-w-h ev-cgame)) 2))
(expect 1 (get-player-result-from-game (m-s-p (m-w-h ev-cgame)) 3))
(expect -1 (get-player-result-from-game (m-s-p (m-w-h ev-cgame)) 4))


