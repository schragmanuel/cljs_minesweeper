(ns minesweeper.events-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]))

(def grid (minesweeper.db/create-grid))

(deftest show-field-test
  (testing "showing field returns grid with visible field"
    (is (true? (-> (minesweeper.events/show-field grid 1 2)
                   (get-in [1 2])
                   (:visible))))))

(run-tests)