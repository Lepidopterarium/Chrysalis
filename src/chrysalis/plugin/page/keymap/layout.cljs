;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  Simon-Claudius Wystrach <mail@simonclaudius.com>
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

(ns chrysalis.plugin.page.keymap.layout
  (:require [chrysalis.ui :as ui :refer [color->hex]]
            [chrysalis.key :as key]

            [chrysalis.plugin.page.keymap.events :as events]

            [re-frame.core :as re-frame]
            [clojure.walk :as walk]
            [clojure.set :as set]))

(defn- key-index [device r c cols]
  (if-let [keymap-layout (get-in device [:keymap :map])]
    (get-in keymap-layout [r c])
    (+ (* r cols) c)))

(defn- current-node? [r c]
  (if-let [target (events/current-target)]
    (let [cr (js/parseInt (.getAttribute target "data-row"))
          cc (js/parseInt (.getAttribute target "data-column"))]
      (and (= r cr) (= c cc)))
    false))

(defn- node-update [device layer node theme interactive?]
  (let [[r c] (->> node :id
                  (re-find #"R(\d+)C(\d+)_keyshape$")
                  rest
                  (map #(js/parseInt % 10)))]
    (if (and r c)
      (let [[cols rows] (get-in device [:meta :matrix])
            index (key-index device r c cols)
            edited? (contains? (events/layout-edits) [layer index])]
        (if interactive?
          (assoc node
                 :class :key
                 :data-row r
                 :data-column c
                 :data-index index
                 :stroke-width (if (current-node? r c)
                                 2
                                 2)
                 :stroke (if (current-node? r c)
                           "#ff0000"
                           "#b4b4b4")
                 :fill (if edited?
                         "rgba(255,0,0,0.5)"
                         "rgba(255,255,255,0)")
                 :on-click (fn [e]
                             (let [target (.-target e)]
                               (events/current-target! target))))))
      node)))

(defn- print-labels
  "Print key labels on the SVG"
  [device layout node layer]
  (let [id (:id (get node 1))
        [_ label] (re-find #"_t_(.*)" id)
        [r c] (->> id
                  (re-find #"R(\d+)C(\d+)") rest
                  (map #(js/parseInt % 10)))]
    (if (and r c)
      (let [[cols rows] (get-in device [:meta :matrix])
            index (key-index device r c cols)
            formatted-key (-> layout (get-in [layer index]) key/format)]
        (assoc node 2 (get formatted-key (keyword (str label "-text")))))
      node)))

(defn layout-svg
  [{:keys [device svg layout props layer]}]
  (walk/prewalk (fn [node]
                  (if (and (vector? node) (= (first node) :text))
                    (print-labels device layout node layer)
                    (if (and (map? node) (get node :id))
                      (node-update device layer node layout (:interactive? props))
                      node)))
                (-> svg
                    (update 1 merge (dissoc props :interactive?))
                    (update 1 set/rename-keys {:viewbox :view-box}))))

(defn <keymap-layout> [{:keys [device svg layout props layer] :as args}]
  (if layout
    [layout-svg args]
    [:i.fa.fa-refresh.fa-spin.fa-5x]))
