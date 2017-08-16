;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  Gergely Nagy <algernon@madhouse-project.org>
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

(ns chrysalis.plugin.page.led.theme
  (:require [chrysalis.ui :as ui :refer [color->hex]]

            [chrysalis.plugin.page.led.events :as events]

            [re-frame.core :as re-frame]
            [clojure.walk :as walk]))

(defn- hex->color [hex]
  (let [r (js/parseInt (.substring hex 1 3) 16)
        g (js/parseInt (.substring hex 3 5) 16)
        b (js/parseInt (.substring hex 5 7) 16)]
    [r g b]))

(defn- key-index [device r c cols]
  (if-let [led-map (get-in device [:led :map])]
    (nth (nth led-map r) c)
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
            color (nth theme index [0 0 0])]
        (if interactive?
          (assoc node
                 :class :key
                 :data-row r
                 :data-column c
                 :data-index index
                 :fill (color->hex color)
                 :stroke-width (if (current-node? r c)
                                 2
                                 2)
                 :stroke (if (current-node? r c)
                           "#ff0000"
                           "#b4b4b4")
                 :on-click (fn [e]
                             (let [target (.-target e)]
                               (events/current-target! target))))
          (assoc node
                 :fill (color->hex color))))
      node)))

(defn prepare [device svg theme props]
  (walk/prewalk (fn [node]
                  (if (and (map? node) (get node :id))
                    (node-update device node theme (:interactive? props))
                    node))
                (-> svg
                    (assoc 1 (assoc (dissoc props :interactive?) :view-box "0 0 1024 640")))))

(defn <led-theme> [device svg theme props]
  (if theme
    (prepare device svg theme props)
    [:i.fa.fa-refresh.fa-spin.fa-5x]))
