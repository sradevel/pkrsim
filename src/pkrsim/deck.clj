(ns pkrsim.deck
  (:require [pkrsim.util :refer :all]))

;; 52 PokderCard Deck
(def card-deck
  [
   "2H" "3H" "4H" "5H" "6H" "7H" "8H" "9H" "TH" "JH" "QH" "KH" "AH"
   "2D" "3D" "4D" "5D" "6D" "7D" "8D" "9D" "TD" "JD" "QD" "KD" "AD"
   "2S" "3S" "4S" "5S" "6S" "7S" "8S" "9S" "TS" "JS" "QS" "KS" "AS"
   "2C" "3C" "4C" "5C" "6C" "7C" "8C" "9C" "TC" "JC" "QC" "KC" "AC"
   ])

(defn new-card-deck
  "Creates a new 52 PokerCard deck."
  []
  card-deck)

(defn new-shuffled-card-deck
  "Creates a new already shuffled 52 PokerCard deck."
  []
  (shuffle (new-card-deck)))

(defn remove-cards-from-deck
  "Removes cards from the deck."
  [deck cards]
  (filter-subvector cards deck))
