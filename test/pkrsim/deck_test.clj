(ns pkrsim.deck-test
  (:require [expectations :refer :all]
            [pkrsim.deck :refer :all]))

(expect 52 (count (new-card-deck)))
(expect 52 (count (new-shuffled-card-deck)))

(def testdeck ["AS" "AD" "AC" "AH"])

;; Removes Cards from a deck.

(expect ["AD" "AC" "AH"] (remove-cards-from-deck testdeck ["AS"]))
(expect ["AC" "AH"]      (remove-cards-from-deck testdeck ["AS" "AD"]))
(expect []               (remove-cards-from-deck testdeck testdeck))
