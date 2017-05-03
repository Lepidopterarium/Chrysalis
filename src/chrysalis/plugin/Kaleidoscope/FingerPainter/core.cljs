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

(ns chrysalis.plugin.Kaleidoscope.FingerPainter.core
  (:require [chrysalis.plugin.page.repl.core :refer [display repl-wrap]]
            [chrysalis.ui :refer [color->hex]]))

(defmethod display :fingerpainter.palette [_ req result device index]
  (when result
    (let [palette (map (fn [spec] (map js/parseInt (.split spec #" ")) ) (remove #{""} (.split result #" *(\d+ \d+ \d+) *")))]
      (repl-wrap req index device
                 [:pre
                  (map (fn [[r g b] index]
                         [:span.badge
                          {:key (str "repl-fingerpainter-palette-" index)
                           :style {:background-color (str "rgb(" r "," g "," b ")")
                                   :color (str "rgb(" (bit-xor 0xff r) "," (bit-xor 0xff g) "," (bit-xor 0xff b) ")")
                                   :display "inline-block"
                                   :margin-right "1em"}}
                          (color->hex [r g b])])
                       palette (range))]))))
