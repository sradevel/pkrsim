(ns pkrsim.simulation
  (:require [pkrsim.game :as g]
            [pkrsim.deck :as d]))

(defn- remove-board-cards-from-deck
  [game]
  (assoc game :deck (d/remove-cards-from-deck (:deck game) (:board game))))

(defn- remove-player-cards-from-deck
  [game]
  (let [player-cards (into [] (map :holecards (:players game)))]
   (assoc game :deck (d/remove-cards-from-deck (:deck game) player-cards))))

(defn- add-shuffled-deck
  [game]
  (assoc game :deck (d/new-shuffled-card-deck)))

(defn- prepare-step
  [game]
  (-> game
      add-shuffled-deck
      remove-board-cards-from-deck
      remove-player-cards-from-deck))

(defn- simulate-step
  [game]
  (vec (map
        (fn [p] {:id (:id p) :result (:result p)})
        (:players (g/play game)))))

(defn simulate-game
  [game]
  (repeatedly #(simulate-step (prepare-step game))))

(defn- count-result
  [e r]
  (let [entry (if (or (nil? e) (empty? e))
                  {:w 0 :l 0 :s 0}
                  e)]
    (cond
      (= r 1) (assoc entry :w (inc (:w entry)))
      (= r -1) (assoc entry :l (inc (:l entry)))
      (= r 0) (assoc entry :s (inc (:s entry))))))

(defn- map-simulation-result
  [a r]
  (reduce (fn [x y]
         (let [key (keyword (str (:id y)))
               e (get x key)
               entry (if (nil? e) {} e)]
           (assoc x key (count-result entry (:result y)))))
       a r))

(defn analyze-game
  [game n]
  (reduce map-simulation-result {} (take n (simulate-game game))))
