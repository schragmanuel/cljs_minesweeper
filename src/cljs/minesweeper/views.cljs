(ns minesweeper.views
  (:require
   [re-frame.core :as rf]
   [minesweeper.subs :as subs]
   [minesweeper.grid :as grid]
   [minesweeper.events :as evt]
   ))

(defn grid-item-component [field x y]
  (let [value (:value field)
        visible (:visible field)]
    [:button.fw-bold
     {:disabled visible
      :on-click #(rf/dispatch [::evt/show-field [x y]])
      :class    (str "grid-item value" value
                     (when visible
                       " visible")
                     (when (and visible (grid/bomb? field))
                       " fas fa-bomb"))}
     (when (and visible (> value 0))
       (str value))
     ]))

(defn grid-item [x y]
  (let [item (rf/subscribe [::subs/grid-item [x y]])]
    (grid-item-component @item x y)))

(defn replay-button []
  [:button.btn.btn-primary
   {:on-click #(rf/dispatch [::evt/replay])}
   "Replay Game"])

(defn show-all-button [finished?]
  [:button.btn.btn-warning
   {:on-click #(rf/dispatch [::evt/show-all])
    :disabled finished?}
   "Show All - Loose the game!"])

(defn status-icon [lost? finished?]
  [:button.btn.m-1 {:class (if lost?
                             "btn-danger"
                             (if finished?
                               "btn-info"
                               "btn-success"))}
   [:i.fas.fs-5 {:class (if lost?
                          "fa-frown"
                          (if finished?
                            "fa-trophy"
                            "fa-smile"))}]])

(defn main-panel []
  (let [name @(rf/subscribe [::subs/name])
        grid-size @(rf/subscribe [::subs/grid-size])
        lost? @(rf/subscribe [::subs/lost])
        finished? @(rf/subscribe [::subs/finished])]
    [:div.container
     {:class (when lost? "lost")}
     [:h1 "Play " name]
     (replay-button) [:span.m-1] (show-all-button finished?)
     (status-icon lost? finished?)
     [:div.mt-2
      (into [:div.grid]
            (for [y (range grid-size)
                  x (range grid-size)]
              (grid-item x y)))]]
    ))

