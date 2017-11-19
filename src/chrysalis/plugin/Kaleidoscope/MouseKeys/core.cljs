;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  James Cash <james.cash@occasionallycogent.com>
;;
;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.
;;
;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns chrysalis.plugin.Kaleidoscope.MouseKeys.core
  (:require [chrysalis.key :as key]
            [clojure.set :as set]))

(defn- mousekeys-processor
  [key code]
  (let [flags (bit-shift-right code 8)
        key-code (bit-and code 0x00ff)]
    (if (= flags (-> 0 (bit-set 6) ; synthetic
                     (bit-set 4))); IS_MOUSE_KEY
      (let [warp-end? (bit-test key-code 6)]
        (-> key
            (assoc
              :plugin :mouse-keys
              :warp? (bit-test key-code 5)
              :warp-end? warp-end?
              :wheel? (bit-test key-code 4))
            (cond->
                warp-end? (assoc :button
                                 (condp #(bit-test %2 %1) key-code
                                   0 :left
                                   1 :right
                                   2 :middle
                                   nil))
                (not warp-end?)
                (assoc :directions
                       (reduce (fn [dirs [bit dir]]
                                 (if (bit-test key-code bit)
                                   (conj dirs dir)
                                   dirs))
                               #{}
                               {0 :up
                                1 :down
                                2 :left
                                3 :right})))))
      key)))

(swap! key/processors conj mousekeys-processor)

(defn directions->arrow
  [directions]
  (condp set/subset? directions
    #{:up :right} "↗"
    #{:up :left} "↖"
    #{:down :right} "↘"
    #{:down :left} "↙"
    #{:up} "↑"
    #{:down} "↓"
    #{:left} "←"
    #{:right} "→"))

(defmethod key/format [:mouse-keys]
  [{:keys [warp? warp-end? wheel? button directions]}]
  {:extra-text "Mouse"
   :primary-text (cond
                   (and warp-end? (not button)) "WarpEnd"
                   warp-end? (str "Btn " (name button))
                   wheel? (str "Scrl " (directions->arrow directions))
                   warp? (str "Warp " (directions->arrow directions))
                   :else (directions->arrow directions))})

(defmethod key/unformat :mouse-keys
  [{:keys [warp? warp-end? wheel? button directions]}]
  (let [flags (-> 0 (bit-set 6) (bit-set 4) (bit-shift-left 8))
        key-code (cond-> 0
                   wheel? (bit-set 4)
                   warp? (bit-set 5)
                   warp-end? (bit-set 6)
                   (some? button) (bit-set (case button
                                             :left 0
                                             :right 1
                                             :middle 2))
                   (seq directions)
                   (as-> <>
                       (reduce bit-set
                               <>
                               (map {:up 0 :down 1 :left 2 :right 3}
                                    directions))))]
    (bit-or flags key-code)))
