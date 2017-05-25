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
  (:require [chrysalis.plugin.page.led.events :as events]))

(defn <color-picker> []
  (if-let [target (events/current-target)]
    (when-let [color (.getAttribute target "fill")]
      [:form.input-group {:on-submit (fn [e]
                                       (.preventDefault e)
                                       #_(let [target (get-in @state [:led :current-target])
                                             index (js/parseInt (.getAttribute target "data-index"))
                                             new-color (get-in @state [:led :new-color])
                                             theme (get-in @state [:led :theme])]
                                         (swap! theme assoc index (hex->color new-color))))}
       [:input.form-control {:type :text
                             :placeholder color
                             :on-change (fn [e]
                                          #_(swap! state assoc-in [:led :new-color] (.-value (.-target e))))}]
       [:tt.input-group-addon {:style {:background-color color}} color]])))
