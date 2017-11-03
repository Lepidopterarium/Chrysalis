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

(ns chrysalis.plugin.Kaleidoscope.LED-Palette-Theme.core
  (:require [chrysalis.command.post-process :as post-process]
            [chrysalis.plugin.page.repl.core :refer [display repl-wrap]]
            [clojure.string :as string]
            [chrysalis.ui :as ui]))

(defmethod post-process/format [:led.palette] [_ text]
  (->> (string/split text #" ")
       (map #(js/parseInt % 10))
       (partition 3)
       (mapv vec)))

(defmethod display :palette [_ req result device index]
  (repl-wrap
    req index device result
    (into [:pre]
          (map-indexed
            (fn [i [r g b]]
              [:span.badge
               {:style {:background-color (str "rgb(" r "," g "," b ")")
                        :color (str "rgb(" (bit-xor 0xff r) ","
                                    (bit-xor 0xff g) ","
                                    (bit-xor 0xff b) ")")
                        :display "inline-block"
                        :border "1px solid black"
                        :margin "1em"}}
               i " " (ui/color->hex [r g b])]))
          (post-process/format :led.palette result))))
