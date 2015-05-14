(ns pkrsim.handeval-test
    (:require [expectations :refer :all]
              [pkrsim.handeval :refer :all]))

;; Test Hands
(def high-seven                   ["2H" "3S" "4C" "5C" "7D"])
(def pair-hand                    ["2H" "2S" "4C" "5C" "7D"])
(def two-pairs-hand               ["2H" "2S" "4C" "4D" "7D"])
(def three-of-a-kind-hand         ["2H" "2S" "2C" "4D" "7D"])
(def four-of-a-kind-hand          ["2H" "2S" "2C" "2D" "7D"])
(def straight-hand                ["2H" "3S" "6C" "5D" "4D"])
(def low-ace-straight-hand        ["2H" "3S" "4C" "5D" "AD"])
(def high-ace-straight-hand       ["TH" "AS" "QC" "KD" "JD"])
(def flush-hand                   ["2H" "4H" "5H" "9H" "7H"])
(def full-house-hand              ["2H" "5D" "2D" "2C" "5S"])
(def straight-flush-hand          ["2H" "3H" "6H" "5H" "4H"])
(def low-ace-straight-flush-hand  ["2D" "3D" "4D" "5D" "AD"])
(def high-ace-straight-flush-hand ["TS" "AS" "QS" "KS" "JS"])

;; Special case: straights will be calculated when the highest card
;; minus the lowest cards will be 4.
;; This is the case for the next hand, but it is not a straight.
(def no-straight-hand-but-straight-value ["2S" "3D" "3D" "5S" "AS"])

;; pull in private function for tests
(def suite             #'pkrsim.handeval/suite)
(def rank              #'pkrsim.handeval/rank)
(def seq-rank          #'pkrsim.handeval/seq-rank)
(def seq-suite         #'pkrsim.handeval/seq-suite)
(def value             #'pkrsim.handeval/hand-value)
(def rank-list-tp-fh-hands #'pkrsim.handeval/rank-list-tp-fh-hands)

;; test suite 
(expect "S" (suite "AS"))
(expect "D" (suite "TD"))

;; test rank
(expect 10 (rank "TD"))
(expect 2  (rank "2S"))

;; test seq-rank
(expect '(2 4 5 9 7)      (seq-rank flush-hand))
(expect '(10 14 12 13 11) (seq-rank high-ace-straight-hand))

;; test seq-suite
(expect '("H" "H" "H" "H" "H") (seq-suite flush-hand))
(expect '("H" "S" "C" "D" "D") (seq-suite high-ace-straight-hand))

;; test hands

; every hand is a high-card
(expect true (high-card? full-house-hand))
(expect true (high-card? flush-hand))

;; Pair Hands
(expect true  (pair? pair-hand))
(expect true  (pair? two-pairs-hand))
(expect false (pair? flush-hand))

;; Two Pair Hands
(expect true  (two-pair? two-pairs-hand))
(expect false (two-pair? flush-hand))
(expect false (two-pair? pair-hand))

;; Three of a kind
(expect true  (three-of-a-kind? three-of-a-kind-hand))
(expect false (three-of-a-kind? flush-hand))
(expect false (three-of-a-kind? pair-hand))

;; Four of a kind
(expect true  (four-of-a-kind? four-of-a-kind-hand))
(expect false (four-of-a-kind? three-of-a-kind-hand))
(expect false (four-of-a-kind? flush-hand))

;; Full-house
(expect true  (full-house? full-house-hand))
(expect false (full-house? flush-hand))
(expect false (full-house? two-pairs-hand))
(expect false (full-house? three-of-a-kind-hand))

;; Flush
(expect true  (flush? flush-hand))
(expect true  (flush? straight-flush-hand))
(expect false (flush? full-house-hand))
(expect false (flush? two-pairs-hand))

;; Straights
(expect true  (straight? high-ace-straight-hand))
(expect true  (straight? low-ace-straight-hand))
(expect true  (straight? high-ace-straight-flush-hand))
(expect true  (straight? low-ace-straight-flush-hand))
(expect false (straight? flush-hand))
(expect false (straight? full-house-hand))
(expect false (straight? no-straight-hand-but-straight-value))

;; Staight-Flushes
(expect false (straight-flush? high-ace-straight-hand))
(expect false (straight-flush? low-ace-straight-hand))
(expect true  (straight-flush? high-ace-straight-flush-hand))
(expect true  (straight-flush? low-ace-straight-flush-hand))
(expect false (straight-flush? flush-hand))
(expect false (straight-flush? full-house-hand))
(expect false (straight-flush? no-straight-hand-but-straight-value))

;; hand values
(expect 0 (value high-seven))
(expect 1 (value pair-hand))
(expect 2 (value two-pairs-hand))
(expect 3 (value three-of-a-kind-hand))
(expect 4 (value high-ace-straight-hand))
(expect 4 (value low-ace-straight-hand))
(expect 5 (value flush-hand))
(expect 6 (value full-house-hand))
(expect 7 (value four-of-a-kind-hand))
(expect 8 (value high-ace-straight-flush-hand))
(expect 8 (value low-ace-straight-flush-hand))

;; rank-hands
(expect [2 7 5 4]   (rank-list-tp-fh-hands ["2H" "2S" "4C" "5C" "7D"]))
(expect [2 13 10 7] (rank-list-tp-fh-hands ["2H" "2S" "KC" "TC" "7D"]))

(expect ["2H" "2S" "4C" "KC" "7D"]
        (find-best-equal-value-hand ["2H" "2S" "4C" "5C" "7D"]
                                    ["2H" "2S" "4C" "KC" "7D"]))

;; sort-hand-for-cmp
;; Hands had to be sorted differently to be compared
;; hand-by-hand.
(expect '(9 7 5 4 2) (sort-card-values flush-hand))

;; 7 card Test hands
(def high-king                    ["KC" "TD" "2H" "3S" "4C" "5C" "7D"])
(def pair-hand-sc                 ["2H" "2S" "4C" "5C" "7D" "KC" "TD"])
(def two-pairs-hand-sc            ["2H" "2S" "4C" "4D" "7D" "KC" "TD"])
(def three-of-a-kind-hand-sc      ["2H" "2S" "2C" "4D" "7D" "KC" "TD"])
(def four-of-a-kind-hand-sc       ["2H" "2S" "2C" "2D" "7D" "KC" "TD"])
(def straight-hand-sc             ["2H" "3S" "6C" "5D" "4D" "KC" "TD"])
(def low-ace-straight-hand-sc     ["2H" "3S" "4C" "5D" "AD" "KC" "TD"])
(def high-ace-straight-hand-sc    ["TH" "AS" "QC" "KD" "JD" "KC" "TD"])
(def flush-hand-sc                ["2H" "4H" "5H" "9H" "7H" "KC" "TD"])
(def full-house-hand-sc           ["2H" "5D" "2D" "2C" "5S" "KC" "TD"])
(def straight-flush-hand-sc       ["2H" "3H" "6H" "5H" "4H" "KC" "TD"])

;; find-best-hand
(expect '("2H" "2S" "7D" "KC" "TD") (find-best-hand pair-hand-sc))

(expect {:hand '("2H" "2S" "7D" "KC" "TD") :value 1}
        (find-best-hand-with-value pair-hand-sc))



