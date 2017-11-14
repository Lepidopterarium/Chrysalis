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

(ns chrysalis.plugin.page.led.palette
  (:require [chrysalis.plugin.page.led.events :as events]))

(defn- rgb-str
  [[r g b]]
  (str "rgb(" r "," g "," b ")"))

(defn- invert-color
  [[r g b]]
  (str "rgb(" (bit-xor 0xff r) "," (bit-xor 0xff g) "," (bit-xor 0xff b) ")"))

(defn <palette>
  []
  [:table
   (into [:tbody]
         (map (fn [r] [:tr r]))
         (->> (events/palette)
             (map-indexed
               (fn [i [r g b :as rgb]]
                 ^{:key i}
                 [:td {:style {:background-color (rgb-str rgb)
                               :color (invert-color rgb)
                               :border (when (= i (events/current-palette-target))
                                         "2px dashed white")
                               :padding "0.5em"
                               :cursor "pointer"}
                       :on-click (fn [_]
                                   (events/current-palette-target! i)
                                   (events/colormap:set-target-color! i))}
                  i]))
             (partition 4)))])
