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
  (:require [chrysalis.plugin.page.led.events :as events]
            [re-frame.core :as re-frame]))

(defonce react-color (js/require "react-color"))

(re-frame/reg-event-db
 :led/picker.update
 (fn [db [_ index color]]
   (let [{:keys [r g b]} (js->clj (:rgb color)
                                  :keywordize-keys true)]
     (when (>= index 0)
       (assoc-in db [:led/theme index] [r g b])))))

(defn <color-picker> []
  (let [picker (.-ChromePicker react-color)
        target (events/current-target)
        index (if target
                (js/parseInt (.getAttribute target "data-index"))
                -1)
        color (if target
                (.getAttribute target "fill")
                "#000000")]
    [:> picker {:color color
                :disable-alpha true
                :triangle :hide
                :on-change (fn [color event]
                             (re-frame/dispatch [:led/picker.update index (js->clj color
                                                                                   :keywordize-keys true)]))}]))
