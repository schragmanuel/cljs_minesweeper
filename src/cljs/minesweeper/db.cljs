(ns minesweeper.db
  (:require
    [cljs.pprint :refer [pprint]]
    [minesweeper.grid :as g]))

(def grid-size 10)

(def default-db
  {:name "Minesweeper"
   :grid-size grid-size
   :grid (g/create-grid grid-size)
   :lost false
   :finished false})

(defn create-grid [grid-size]
  (g/create-grid grid-size))

(defn show-all [db]
  (assoc db :grid (g/show-all (:grid db))
            :lost true
            :finished true))

(defn reveal-fields [coords grid]
  (reduce (fn [g [x y]] (->> (g/get-field x y g)
                             (g/make-visible)
                             (assoc-in g [y x])))
          grid
          coords))

(defn show-field [db x y]
  (let [grid (:grid db)
        field (g/get-field x y grid)]
    (if (g/bomb? field)
      (show-all db)
      (let [new-grid (-> (g/to-reveal x y grid)
                          (reveal-fields grid))
            finished? (g/all-visible? new-grid)]
        (assoc db :grid (if finished?
                          (g/show-all grid)
                          new-grid)
                  :finished finished?)))))

(defn replay [db]
  (-> (assoc db :grid (create-grid (:grid-size db)))
       (assoc :lost false
              :finished false)))