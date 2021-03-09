(ns minesweeper.grid)

;; grid creation
(defrecord Field [value visible flagged])

(defn- xlength [grid]
  (count (first grid)))

(defn- ylength [grid]
  (count grid))

(defn- outside-grid? [x y grid]
  (or
    (neg-int? x)
    (neg-int? y)
    (>= y (ylength grid))
    (>= x (xlength grid))))

(def bomb-value -1)

(defn bomb? [field]
  (= bomb-value (get field :value)))

(defn flagged? [field]
  (get field :flagged))

(defn- grid-row [nbr]
  (->> (repeatedly nbr #(->Field 0 false false))
       (vec)))

(defn- random-xy [nbr]
  [(rand-int nbr) (rand-int nbr)])

(defn- get-field [x y grid]
  (get-in grid [y x]))

(defn- put-field [x y grid field]
  (if (outside-grid? x y grid)
    grid
    (assoc-in grid [y x] field)))

(defn- border-around [[tlx tly] [brx bry]]
  (let [left    (- tlx 1)
        top     (- tly 1)
        right   (+ brx 1)
        bottom  (+ bry 1)]
    (->> (for [x (range left (+ right 1))
               y (range top (+ bottom 1))]
           [x y])
         (filter (fn [[x y]] (or (= x left) (= x right) (= y top) (= y bottom)))))))

(defn- neighbours
  ([x y] (neighbours [[x y]]))
  ([rect-border-coords]
   (let [top-left (reduce
                    (fn [[minx miny] [x y]] (if (or (< x minx) (< y miny))
                                              [x y]
                                              [minx miny]))
                    (first rect-border-coords)
                    rect-border-coords)
         bottom-right (reduce
                        (fn [[maxx maxy] [x y]] (if (or (> x maxx) (> y maxy))
                                                  [x y]
                                                  [maxx maxy]))
                        (first rect-border-coords)
                        rect-border-coords)]
     (border-around top-left bottom-right))))

(defn- in-grid
  ([coords grid]
   (filter (fn [[x y]] (not (outside-grid? x y grid))) coords)))

(defn- grid-neighbours
  ([x y grid] (in-grid (neighbours x y) grid)))

(defn- neighbour? [[x1 y1] [x2 y2]]
  (and (not (and (= x1 x2) (= y1 y2)))
       (<= (- x1 1) x2 (+ x1 1))
       (<= (- y1 1) y2 (+ y1 1))))

(defn- one-neighbour? [point coll]
  (->> (filter #(neighbour? point %) coll)
       (first)
       (nil?)
       (not)))

(defn- bomb-nearby [field]
  (if (bomb? field)
    field
    (->> (get field :value)
         (inc)
         (assoc field :value))))

(defn- put-bomb [grid [x y]]
  (if (bomb? (get-field x y grid))
    grid
    (reduce
      (fn [g [nx ny]] (->> (bomb-nearby (get-field nx ny g))
                           (put-field nx ny g)))
      (put-field x y grid (->Field bomb-value false false))
      (grid-neighbours x y grid))))

(defn- create-empty-grid [grid-size]
  (vec (repeatedly grid-size #(grid-row grid-size))))

(defn create-grid [grid-size]
  (let [grid (create-empty-grid grid-size)
        bomb-coords (repeatedly grid-size #(random-xy grid-size))]
    (reduce put-bomb grid bomb-coords)))


;; playing
(defn make-visible [field]
  (assoc field :visible true))

(defn flag-field [field]
  (assoc field :flagged true))

(defn- visible? [field]
  (get field :visible))

(defn show-all [grid]
  (for [row grid]
    (->> row
         (map make-visible))))

(defn zero-field?
  ([x y grid]
   (-> (get-field x y grid)
       (zero-field?)))
  ([field]
   (= 0 (get field :value))))

(defn- to-reveal [x y grid]
  "Alle leeren Felder in der direkten Umgebung, falls das Feld selbst auch leer ist.
  Ansonsten wird nur das Feld selbst zurückgegeben"
  (let [start [[x y]]]
    (if (zero-field? x y grid)
      (loop [context {:all-zeroes    start
                      :zeroes-before start}
             current-border start]
        (let [{:keys [all-zeroes zeroes-before]} context
              next-border (neighbours current-border)
              current-coords (in-grid current-border grid)]
          (if (= (count zeroes-before) 0)
            all-zeroes
            (recur (let [zeroes (->> (in-grid next-border grid)
                                     (filter (fn [[x y]] (zero-field? x y grid)))
                                     (filter (fn [point] (one-neighbour? point current-coords))))]
                     {:all-zeroes    (concat all-zeroes zeroes)
                      :zeroes-before zeroes})
                   next-border))))
      start)))

(defn all-visible? [grid]
  (->> (flatten grid)
       (filter #(not (bomb? %1)))
       (some #(not (visible? %1)))
       (not)))

;(def grid (create-grid 10))