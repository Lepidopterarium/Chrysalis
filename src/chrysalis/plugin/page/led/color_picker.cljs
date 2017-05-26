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

(ns chrysalis.plugin.page.led.color-picker
  (:require [chrysalis.plugin.page.led.events :as led.events]
            [chrysalis.plugin.page.led.color-picker.events :as picker.events]))

(defonce react-color (js/require "react-color"))

(defn <color-picker> []
  (let [picker (.-ChromePicker react-color)
        target (led.events/current-target)
        index (if target
                (js/parseInt (.getAttribute target "data-index"))
                -1)
        color (if target
                (.getAttribute target "fill")
                "#000000")]
    [:> picker {:color color
                :disable-alpha true
                :triangle :hide
                :on-change (fn [color _]
                             (when-not (neg? index)
                               (picker.events/update! index color)))
                :on-change-complete (fn [color _]
                                      (when-not (neg? index)
                                        (picker.events/update! index color)
                                        (when (led.events/live-update?)
                                          (led.events/theme:upload!))))}]))
