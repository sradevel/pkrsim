(ns pkrsim.handeval
  (:require [pkrsim.util :as u]))

;;
;; local private Utility functions

;; Ranks of the face-cards.
(def face-ranks {\T 10 \J 11 \Q 12 \K 13 \A 14})

(defn- suite
  "Returns the suite of the Card."
  [[_ suite]]
  (str suite))

(defn- rank
  "Returns the rank of the Card"
  [[rank _]]
  (if (Character/isDigit rank)
    (Integer/valueOf (str rank))
    (face-ranks rank)))

;;
;; private helper functions
;;

(defn- seq-rank [hand] (map rank hand))
(defn- seq-suite [hand] (map suite hand))

(defn- count-same-ranks?
  "Returns if hand contains [n] cards of same rank"
  [hand n]
  (let [ranks (seq-rank hand)
        freqs (frequencies ranks)]
    (= (apply max (vals freqs)) n)))

(defn- compare-histogram-value
  "Compares a Hand Rank Histogramm against the given value n."
  [hand n]
  (let [histogram (vals (frequencies (seq-rank hand)))]
    (= (seq (sort histogram)) (seq (sort n)))))

(defn- straight-value?
  "A hand is a straight if the max rank minus the min rank is 4"
  [rank-values]
  (let [sorted-ranks (sort rank-values)
        min-rank (apply min sorted-ranks)
        max-rank (apply max sorted-ranks)]
    (= (- max-rank min-rank) 4)))

;;
;; hand evaluators

(defn high-card?
  "Every hand is a high-card hand."
  [hand]
  true)

(defn pair?
  "Returns wether the hand is a pair or not"
  [hand]
  (count-same-ranks? hand 2))

(defn three-of-a-kind?
  "Returns wether the hand is a three of a kind."
  [hand]
  (compare-histogram-value hand '(1 1 3)))

(defn four-of-a-kind?
  "Returns wether the hand is four of a kind."
  [hand]
  (compare-histogram-value hand '(1 4)))

(defn two-pair?
  "Returns wether the hand is two-pair."
  [hand]
  (compare-histogram-value hand '(1 2 2)))

(defn full-house?
  "Returns wether the hand is a full-house."
  [hand]
  (compare-histogram-value hand '(2 3)))

(defn flush?
  "Returns wether the hand is flush."
  [hand]
  (= (apply max (vals (frequencies (seq-suite hand)))) 5))

(defn straight?
  "Returns wether the hand is a straight."
  [hand]
  (if (or (pair? hand) (two-pair? hand) (three-of-a-kind? hand))
    false
    (let [rank-values (seq-rank hand)]
      (or (straight-value? rank-values)
            (straight-value? (replace {14 1} rank-values))))))

(defn straight-flush?
  "Returns wether the hand is a straight-flush."
  [hand]
  (and (straight? hand) (flush? hand)))


;;
;; Evaluate the hand value
;;

(defn hand-value
  "Defines the value of a hand."
  [hand]
  (let [checkers #{[high-card? 0] [pair? 1] [two-pair? 2]
                 [three-of-a-kind? 3] [straight? 4] [flush? 5]
                 [full-house? 6] [four-of-a-kind? 7]
                 [straight-flush? 8]}]
    (apply max (map (fn [[f v]]
                      (if (f hand)
                        v
                        0)) checkers))))

;; TODO: Fix name of the Method.
(defn- rank-list-tp-fh-hands
  "Creates a vector of the card ranks for Two-Pair or Full-house hands.
  Notice: Only one value for the pair or set of the hand will be included
  in the list."
  [hand]
  (let [freq (frequencies (seq-rank hand))
        freq-list (distinct (reverse (sort (vals freq))))]
    (reduce #(into %1 (reverse (sort (u/get-keys-by-val %2 freq)))) [] freq-list)))

(defn sort-card-values
  "Returns a vector of the card values. Notice: Pairs, Two-pairs, trips and 
  four-of -a-kind values appear only once in the vector. Vector can be used
  for easy card-by-card hand comparisons."
  [hand]
  (cond
    (or (full-house? hand)
        (two-pair? hand)
        (pair? hand)
        (three-of-a-kind? hand)
        (four-of-a-kind? hand)) (rank-list-tp-fh-hands hand)
        :else (reverse (sort (seq-rank hand)))))

(defn- hand-cmp
  "Compares two hands card by card."
  [hand-1 hand-2]
  (let [r-hand-1 (sort-card-values hand-1)
        r-hand-2 (sort-card-values hand-2)
        card-values (map vector r-hand-1 r-hand-2)
        cmp (fn [[v1 v2]] (compare v1 v2))
        iterator (fn iterator [card-tuples]
                   (cond
                     (empty? card-tuples) 0
                     (= (cmp (first card-tuples)) 0) (iterator (rest card-tuples))
                     :else (cmp (first card-tuples))))]
    (iterator card-values)))

(defn find-best-equal-value-hand
  "Finds the best hand out of equal valued hands."
  [& hands]
  (first (reverse (sort #(hand-cmp %1 %2) hands))))

(defn- permutate-list
  [coll]
  (reduce (fn [val x] (conj val (filter #(not= %1 x) coll))) [] coll))

(defn- tokenize-seven-card-hand
  [hand]
  (let [tokenize (permutate-list hand)]
    (distinct (reduce (fn [val x] (into val (permutate-list x))) [] tokenize))
    ))

(defn find-best-hand-with-value
  "Finds the best hand out of a 7 card hand. Returns a Map with the 
  best hand and their value."
  [hand]
  (let [hand-permutations (tokenize-seven-card-hand hand)
        hand-values (reduce #(assoc %1 %2 (hand-value %2)) {} hand-permutations)
        best-hand-value (apply max (vals hand-values))]
    {:hand (vec (apply find-best-equal-value-hand
                       (u/get-keys-by-val best-hand-value hand-values)))
     :value best-hand-value}))

(defn find-best-hand
  "Finds the best hand out of a 7 card hand. "
  [hand]
  (:hand (find-best-hand-with-value hand)))
