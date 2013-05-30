(ns map-experiments.smart-maps.bijection
  (:require [map-experiments.smart-maps.protocol :refer :all])
  (:import [clojure.lang
            IPersistentMap IPersistentSet IPersistentCollection IEditableCollection ITransientMap ITransientSet ILookup IFn IObj IMeta Associative MapEquivalence Seqable MapEntry SeqIterator]))

; Invertible map that preserves a bijective property amongst its elements.
(deftype Bijection [metadata
                    ^IPersistentMap active
                    ^IPersistentMap mirror]
  Invertible (inverse [this] (Bijection. metadata mirror active))
  IPersistentMap
  (assoc [this k v]
         (Bijection. metadata
                     (assoc (dissoc active (get mirror v)) k v)
                     (assoc (dissoc mirror (get active k)) v k)))
  (without [this k] (disj this [k (get active k)]))
  (iterator [this]
    (SeqIterator. (seq this)))
  IPersistentCollection
  (cons [this x]
        (if (and (sequential? x) (= 2 (count x)))
            (let [[k v] x]
                 (assoc this k v))
            (throw (IllegalArgumentException.
                     "Vector arg to map conj must be a pair"))))
  (equiv [this o] 
         (or (and (isa? (class o) Bijection)
                  (= active (.active ^Bijection o)))
             (= active o)))
  (count [this] (count active))
  (empty [this] (Bijection. metadata (empty active) (empty mirror)))
  IPersistentSet
  (disjoin [this [k v]]
           (if (and (contains? active k) (contains? mirror v))
               (Bijection. metadata (dissoc active k) (dissoc mirror v))
               this))
  IObj (withMeta [this new-meta] (Bijection. new-meta active mirror))
  ; Boilerplate map-like object implementation code. Common to all the mirrored maps, and also to SetMap (although SetMap uses differing field names).
  Associative
  (containsKey [this k] (contains? active k))
  (entryAt     [this k] (find active k))
  ILookup
  (valAt [this k]           (get active k))
  (valAt [this k not-found] (get active k not-found))
  Seqable (seq      [this]   (seq active))
  IFn     (invoke   [this k] (get active k))
  IMeta   (meta     [this]   metadata)
  Object  (toString [this]   (str active))
  MapEquivalence)

(defn bijection
  "Creates a Bijection, which is an invertible map that preserves a bijective (1-to-1) mapping. That is, both keys and values are guaranteed to be unique; assoc overwrites any extant keys or values, also removing their associated pairings."
  ([] (Bijection. nil (hash-map) (hash-map)))
  ([& keyvals]
   (apply assoc (bijection) keyvals)))