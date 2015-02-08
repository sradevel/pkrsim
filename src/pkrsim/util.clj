(ns pkrsim.util)

(defn get-keys-by-val
  "Returns a list of keys from a given value of a map."
  [v map]
  (keep #(when (= (val %) v)
           (key %)) map))
