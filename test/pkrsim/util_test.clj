(ns pkrsim.util-test
    (:require [expectations :refer :all]
              [pkrsim.util :refer :all]))

(def test-map-1 {2 10 3 11})
(def test-map-2 {2 10 3 10})

(expect '(2) (get-keys-by-val 10 test-map-1))
(expect '(3 2) (get-keys-by-val 10 test-map-2))
