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
            [clojure.walk :as walk]))

(defn- key-index [device r c cols]
  (if-let [keymap-layout (get-in device [:keymap :map])]
    (nth (nth keymap-layout r) c)
    (+ (* r cols) c)))

(defn- current-node? [r c]
  (if-let [target (events/current-target)]
    (let [cr (js/parseInt (.getAttribute target "data-row"))
          cc (js/parseInt (.getAttribute target "data-column"))]
      (and (= r cr) (= c cc)))
    false))

(defn- node-update [device node theme interactive?]
  (let [[r c] (map js/parseInt (rest (re-find #"R(\d+)C(\d+)_keyshape$" (:id node))))]
    (if (and r c)
      (let [[cols rows] (get-in device [:meta :matrix])
            index (key-index device r c cols)
            ]
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
                 :on-click (fn [e]
                             (let [target (.-target e)]
                               (events/current-target! target))))))
      node)))



(defn- print-labels
  "Print key labels on the SVG"
  [device node]

  (let [id (:id (get node 1))
        label (last (re-find #"_t_(.*)" id))
        [r c] (map js/parseInt (rest (re-find #"R(\d+)C(\d+)" id)))]


    (if (and r c)
      (let [[cols rows] (get-in device [:meta :matrix])
            index (key-index device r c cols)]

        (condp = label
          "primary"  (assoc node 2 (:primary-text (key/format(((events/layout) (events/layer)) index))))
          "secondary" (assoc node 2 (:secondary-text (key/format(((events/layout) (events/layer)) index))))
          "extra" (assoc node 2 (:extra-text (key/format(((events/layout) (events/layer)) index)))))
        )
      node))
  )



(defn prepare [device svg layout props]
  (walk/prewalk (fn [node]

                  (if (and (vector? node) (= (first node) :text))
                    (print-labels device node)
                    (if (and (map? node) (get node :id))
                      (node-update device node layout (:interactive? props))
                      node))
                  )

                (-> svg
                    (assoc 1 (assoc (dissoc props :interactive?) :view-box "0 0 1024 640")))))



(defn <keymap-layout> [device svg layout props]
  (if layout
    (prepare device svg layout props)
    [:i.fa.fa-refresh.fa-spin.fa-5x]))
