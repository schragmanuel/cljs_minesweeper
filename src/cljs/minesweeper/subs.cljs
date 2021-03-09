(ns minesweeper.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::grid-size
  (fn [db]
    (:grid-size db)))

(re-frame/reg-sub
  ::lost
  (fn [db]
    (:lost db)))

(re-frame/reg-sub
  ::finished
  (fn [db]
    (:finished db)))

(re-frame/reg-sub
  ::grid-item
  (fn [db [_ [x y]]]
    (let []
      (-> (nth (:grid db) y)
          (nth x)))
    ))
