;;
;;
;;  Copyright 2013 Netflix, Inc.
;;
;;     Licensed under the Apache License, Version 2.0 (the "License");
;;     you may not use this file except in compliance with the License.
;;     You may obtain a copy of the License at
;;
;;         http://www.apache.org/licenses/LICENSE-2.0
;;
;;     Unless required by applicable law or agreed to in writing, software
;;     distributed under the License is distributed on an "AS IS" BASIS,
;;     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;;     See the License for the specific language governing permissions and
;;     limitations under the License.
;;
;;

(ns pigpen.fold-test
  (:use clojure.test)
  (:require [pigpen.util :refer [test-diff pigsym-zero pigsym-inc]]
            [pigpen.core :as pig]
            [pigpen.fold :as fold]))

(deftest test-seq
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/seq) data)]
    (is (= (pig/dump command)
           [[1 2 3 4]]))))

(deftest test-map
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/map #(* % %))) data)]
    (is (= (pig/dump command)
           [[1 4 9 16]]))))

(deftest test-mapcat
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/mapcat (fn [x] [(inc x) (dec x)]))) data)]
    (is (= (pig/dump command)
           [[2 0 3 1 4 2 5 3]]))))

(deftest test-filter
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/filter even?)) data)]
    (is (= (pig/dump command)
           [[2 4]]))))

(deftest test-take
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/take 2)) data)]
    (is (= (pig/dump command)
           [[4 3]]))))

(deftest test-first
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/first)) data)]
    (is (= (pig/dump command)
           [4]))))

(deftest test-last
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/last)) data)]
    (is (= (pig/dump command)
           [1]))))

(deftest test-sort
  (let [data (pig/return [2 4 1 3 2 3 5])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/sort)) data)]
    (is (= (pig/dump command)
           [[1 2 2 3 3 4 5]])))
  
  (let [data (pig/return [2 4 1 3 2 3 5])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/sort >)) data)]
    (is (= (pig/dump command)
           [[5 4 3 3 2 2 1]]))))

(deftest test-sort-by
  (let [data (pig/return [{:foo 1 :bar "d"}
                          {:foo 2 :bar "c"}
                          {:foo 3 :bar "b"}
                          {:foo 4 :bar "a"}])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/sort-by :bar)) data)]
    (is (= (pig/dump command)
           [[{:foo 4, :bar "a"}
             {:foo 3, :bar "b"}
             {:foo 2, :bar "c"}
             {:foo 1, :bar "d"}]])))
  
  (let [data (pig/return [{:foo 1 :bar "d"}
                          {:foo 2 :bar "c"}
                          {:foo 3 :bar "b"}
                          {:foo 4 :bar "a"}])
        command (pig/fold (->>
                            (fold/seq)
                            (fold/sort-by :bar (comp - compare))) data)]
    (is (= (pig/dump command)
           [[{:foo 1, :bar "d"}
             {:foo 2, :bar "c"}
             {:foo 3, :bar "b"}
             {:foo 4, :bar "a"}]]))))

(deftest test-juxt
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/juxt (fold/count) (fold/sum) (fold/avg)) data)]
    (is (= (pig/dump command)
           [[4 10 5/2]]))))

(deftest test-count
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/count) data)]
    (is (= (pig/dump command)
           [4]))))

(deftest test-count-if
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/count-if even?) data)]
    (is (= (pig/dump command)
           [2])))
  
  (let [data (pig/return [1 nil 2 nil 3 nil 4])
        command (pig/fold (fold/count-if identity) data)]
    (is (= (pig/dump command)
           [4]))))

(deftest test-sum
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/sum) data)]
    (is (= (pig/dump command)
           [10]))))

(deftest test-sum-by
  (let [data (pig/return [{:foo 1 :bar "d"}
                          {:foo 2 :bar "c"}
                          {:foo 3 :bar "b"}
                          {:foo 4 :bar "a"}])
        command (pig/fold (fold/sum-by :foo) data)]
    (is (= (pig/dump command)
           [10]))))

(deftest test-sum-if
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/sum-if even?) data)]
    (is (= (pig/dump command)
           [6])))
  
  (let [data (pig/return [1 nil 2 nil 3 nil 4])
        command (pig/fold (fold/sum-if identity) data)]
    (is (= (pig/dump command)
           [10]))))

(deftest test-avg
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/avg) data)]
    (is (= (pig/dump command)
           [5/2]))))

(deftest test-avg-by
  (let [data (pig/return [{:foo 1 :bar "d"}
                          {:foo 2 :bar "c"}
                          {:foo 3 :bar "b"}
                          {:foo 4 :bar "a"}])
        command (pig/fold (fold/avg-by :foo) data)]
    (is (= (pig/dump command)
           [5/2]))))

(deftest test-avg-if
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/avg-if even?) data)]
    (is (= (pig/dump command)
           [3])))
  
  (let [data (pig/return [1 nil 2 nil 3 nil 4])
        command (pig/fold (fold/avg-if identity) data)]
    (is (= (pig/dump command)
           [5/2]))))

(deftest test-top
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/top 2) data)]
    (is (= (pig/dump command)
           [[1 2]])))
  
  (let [data (pig/return [1 2 3 4])
        command (pig/fold (fold/top > 2) data)]
    (is (= (pig/dump command)
           [[4 3]]))))

(deftest test-top-by
  (let [data (pig/return [{:foo 1 :bar "d"}
                          {:foo 2 :bar "c"}
                          {:foo 3 :bar "b"}
                          {:foo 4 :bar "a"}])
        command (pig/fold (fold/top-by :bar 2) data)]
    (is (= (pig/dump command)
           [[{:foo 4, :bar "a"}
             {:foo 3, :bar "b"}]])))
  
  (let [data (pig/return [{:foo 1 :bar "d"}
                          {:foo 2 :bar "c"}
                          {:foo 3 :bar "b"}
                          {:foo 4 :bar "a"}])
        command (pig/fold (fold/top-by :bar (comp - compare) 2) data)]
    (is (= (pig/dump command)
           [[{:foo 1, :bar "d"}
             {:foo 2, :bar "c"}]]))))

(deftest test-min
  (let [data (pig/return [2 1 4 3])
        command (pig/fold (fold/min) data)]
    (is (= (pig/dump command)
           [1]))))

(deftest test-min-key
  (let [data (pig/return [{:foo 2 :bar "c"}
                          {:foo 1 :bar "d"}
                          {:foo 4 :bar "a"}
                          {:foo 3 :bar "b"}])
        command (pig/fold (fold/min-key :foo) data)]
    (is (= (pig/dump command)
           [{:foo 1 :bar "d"}]))))

(deftest test-max
  (let [data (pig/return [2 1 4 3])
        command (pig/fold (fold/max) data)]
    (is (= (pig/dump command)
           [4]))))

(deftest test-max-key
  (let [data (pig/return [{:foo 2 :bar "c"}
                          {:foo 1 :bar "d"}
                          {:foo 4 :bar "a"}
                          {:foo 3 :bar "b"}])
        command (pig/fold (fold/max-key :foo) data)]
    (is (= (pig/dump command)
           [{:foo 4 :bar "a"}]))))