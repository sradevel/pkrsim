(ns pkrsim.util)

(defn get-keys-by-val
  "Returns a list of keys from a given value of a map."
  [v map]
  (keep #(when (= (val %) v)
           (key %)) map))

(defn filter-subvector
  "Filters the given subvector out of the given vector."
  [subvec v]
    (let [iterator (fn iter [subvec c]
                   (cond
                     (empty? subvec) true
                     (not= c (first subvec)) (iter (rest subvec) c)
                     :else false))]
    (vec (filter #(iterator subvec %) v))))

(defn take-into
  "Takes up to max elements from from into in."
  [max in from]
  (into in (take (- max (count in)) from)))
