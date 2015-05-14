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

(defn- prepare-game
  [game]
  (-> game
      add-shuffled-deck
      remove-board-cards-from-deck
      remove-player-cards-from-deck))

(defn- map-simulation-result
  [a r]
  (let [k {1 :w 0 :s -1 :l}
        count-result #(if (empty? %1)
                        {:w 0 :l 0 :s 0}
                        (merge-with + %1 {(k %2) 1}))]
    (reduce (fn [x y]
         (let [key (keyword (str (:id y)))
               e (get x key)
               entry (if (nil? e) {} e)]
           (assoc x key (count-result entry (:result y)))))
       a r)))

(defn simulate-game
  "Simulates a partial played game n-times. Returns a list
   of all simualted games with their results. Simulation tries
  to parallelize the game evaluation."
  [game n]
  (let [steps (repeatedly n #(prepare-game game))]
    (pmap g/play steps)))

(defn sumerize-game-results
  "Expects a list of played-games. Games had to be played to the end. 
   Returns a map containing a win, loose and split-pot sumary for every 
   player in the game list."
  [played-games]
  (reduce map-simulation-result
          {}
          (map
           (fn [g]
             (map
              (fn [p] {:id (:id p) :result (:result p)})
              (:players g)))
           played-games)))
