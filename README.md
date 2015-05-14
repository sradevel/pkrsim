# pokersim

Simulates poker hands (Texas Holdem Hands). Results can be used to
calculate static information about the hands.

The lib can also be used für poker hand Evaluation.

## Preamble

The purpose of this lib is primary the attempt leraning myself a bit
of Clojure. So dont expect production code at all. If you have
sugestions for improving the code, feel free to drop me line or open
an issue.

## Usage

### Installation

Add the following dependency to your project.clj file:

    [pkrsim "0.0.1-SNAPSHOT"]

### Implementation Notes

The implementation of game evaluation is a rather dull brute-force
attack. So dont expect a good performance.

There are more sophisticated algorithms out there.

### Quickstart

```clojure
(require [pksrim.simulation :as s])

(def test-game {
  :players [
    {:id "John" :holecards ["AS" "AD"]}
    {:id "Sam" :holecards ["KS" "KD"]}
  ]
  })

(s/sumerize-game-results (simulate-game test-game 1000))

```

This will run the specified test-game 1000 times and sumerizes the
game results. 

### Data Structure

#### Card Representation

All cards are represented by strings. 

```clojure
(def ace-of-hearts "AH")
(def 10-of-spades  "10S")
(def jack-of-cross "JC")
(def 2-of-diamonds "2D")
;; and so on
```

#### Game

You can specify a game in the following way:

```clojure
(def game { ;; The hole game-structures is simply a map
  :board ["JD" "JS" "KS"] ;; optional drawn board cards
  :players [  ;; players vector
    ;; Every player is a map and must have at least an id and the
    ;; a vector containing the holecards
    {:id "John" :holecards ["AS" "AD"]}
    ... ;; more players
  ]
})
```

### Hand Evaluation

You can use the lib as a Hand Evaluation Tool.

#### Hand Eval functions

A poker hand consists of a vector of 5 cards (see Card Representation
for details on how to construct a card.) Example:

    ["AS" "JD" "10D" "2H" "3D"]

Available evaluation functions:

Function         | Description
-----------------|------------
high-card?       | Every hand is a high card hand.
pair?            | Evaluates true if a hand is a pair.
two-pair?        | Evaluates true if a hand is a Two Pair.
three-of-a-kind? | Evaluates true if a hand is a set.
straight?        | Evaluates true if it is a straight.
flush?           | Evaluates true if a hand is a flush.
full-house?      | Evaluates true if a hand is a full-house.
four-of-a-kind?  | Evaluates true if a hand is four of a kind
straight-flush?  | Evaluates true if a hand is a straight flush.

_Note: If an evaluations function returns true, it doesn't mean it is
the highest possible outcome of the evaluation. E.g. a straight flush
will also evaluates to true on a flush. Use the find-best-hand
function to explicitly define the highest value._

#### hand-value

Evaluates the given hand and returns it value. Values are:

Value | Hand Evaluation
:----:|----------------
0     | High Card
1     | Pair
2     | Two Pair
3     | Three of a kind
4     | Straight
5     | Flush
6     | Full House
7     | Four of a kind
8     | Straight Flush

#### find-best-hand

Finds the best hand out of a 7 card vector.

```clojure
(def hand ["AS" "AD" "10S" "KD" "KS" "2C" "3C"])

(find-best-hand hand)
-> ["AS" "AD" "KD" "KS" "10S"]
```

#### find-best-hand-with-value

Same as _find-best-hand_ but returns a map with the hand and the value
of best found hand.

```clojure
(def hand ["AS" "AD" "10S" "KD" "KS" "2C" "3C"])

(find-best-hand-with-value hand)
-> {:hand ["AS" "AD" "KD" "KS" "10S"] :value 2}
```

#### find-best-equal-value-hand

Finds the best hand (e.g the best flush) out of a 2 or more equal
valued hands.

```clojure
(def hand-1 ["AS" "10S" "QD" "KS" "JC" "3C" "8C"])
(def hand-2 ["10S" "QD" "KS" "JC" "3C" "8C" "9S"])

(find-best-equal-value-hand hand-1 hand-2)
-> ["AS" "10S" "QD" "KS" "JC"]
```

### Simulation

The lib can be used to simulate a given hand many times to calculate
hand odds or other statsitically relevant informations.

_Note: The lib provides only the game evaluation and the bare
simulation. Calculation of various informations is up to you._

#### simulate-game

Simulates the given game n-times. It therefore draws the remaining
cards out of a randomly shuffled deck and evaluates the game.

The result is a list of all evaluated games. The Players will be
marked with a _:result_ keyword. Results:

Result | Meaning
:-----:|--------
-1     | Player looses
1      | Player wins
0      | Player splits pot with another player

It tries to parallize the game evaluation. 

```clojure
(def game { 
  :players [  
    {:id "John" :holecards ["AS" "AD"]}
    {:id "Sam"  :holecards ["KS" "KD"]}
  ]
  })

(simulate-game game 1000)
-> (list of 1000 played and evaluated games.)
```

#### sumerize-game-results

Sample for an possible game result evaluation. It takes a list of
played games and sumerize the game results.

```clojure
(def game { 
  :players [  
    {:id "John" :holecards ["AS" "AD"]}
    {:id "Sam"  :holecards ["KS" "KD"]}
  ]
  })

(sumerize-game-results (simulate-game game 1000))
-> {:Sam {:w 265, :s 5, :l 729}, :John {:w 729, :s 5, :l 265}}
```

## License

Copyright © 2015 Sascha Raabe

Distributed under the MIT License
