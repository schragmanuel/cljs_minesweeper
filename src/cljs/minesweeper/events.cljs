(ns minesweeper.events
  (:require
   [re-frame.core :as re-frame]
   [minesweeper.db :as state]
   [cljs.pprint :refer [pprint]]
   ))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
   state/default-db))

(re-frame/reg-event-db
  ::replay
  (fn [db [_ _]]
    (state/replay db)))

(re-frame/reg-event-db
  ::show-field
  (fn [db [_ [x y]]]
    (state/show-field db x y)))

(re-frame/reg-event-db
  ::show-all
  (fn [db _]
    (state/show-all db)))