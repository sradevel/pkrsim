(ns pkrsim.util-test
    (:require [expectations :refer :all]
              [pkrsim.util :refer :all]))


;;
;; get-keys-by-val
;;
(def test-map-1 {2 10 3 11})
(def test-map-2 {2 10 3 10})

(expect '(2) (get-keys-by-val 10 test-map-1))
(expect '(3 2) (get-keys-by-val 10 test-map-2))

;;
;; filter-subvector
;;
(def test-vec-1 ["a" "b" "c" "d"])
(def test-vec-2 [1 2 3 4])

(expect ["a" "c" "d"] (filter-subvector ["b"] test-vec-1))
(expect []            (filter-subvector test-vec-1 test-vec-1))
(expect [1 2]         (filter-subvector [3 4] test-vec-2))
(expect test-vec-1    (filter-subvector [] test-vec-1))
(expect [1 2]         (filter-subvector [3 4 5] test-vec-2))
(expect test-vec-1    (filter-subvector ["na" "ab"] test-vec-1))

;;
;; take-into
;;

(def test-vec-3 [1 2 3 4])
(def test-vec-4 [5 6 7 8])

(expect [1 2 3 4 5 6] (take-into test-vec-3 6 test-vec-4))
(expect [1 2 3 4]     (take-into test-vec-3 0 test-vec-4))
(expect [1 2 3 4]     (take-into test-vec-3 2 []))
(expect [5 6 7 8]     (take-into [] 4 test-vec-4))
(expect []            (take-into [] 0 []))


