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

(ns chrysalis.app
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [<!]]

            [chrysalis.hardware :as hardware]
            [chrysalis.command :as command]

            [chrysalis.command.LEDControl]

            [chrysalis.hardware.model01]
            [chrysalis.hardware.shortcut]))

(enable-console-print!)

(defonce state (atom {:devices []}))

(defn <device> [device]
  [:div.col-sm-6 {:key (:comName device)}
   [:div.card
    [:div.card-block
     [:div.card-text
      [:p
       "[Image comes here]"]
      [:p
       (get-in device [:meta :name])]]]
    [:div.card-footer.text-muted
     [:button.btn.btn-primary {:type "button" :data-device (:comName device)} "Select"]]]])

(defn <available-devices> []
  [:div
   [:div.row.justify-content-center
    [:div.col-12.text-center
     [:h2 "Available devices"]]]
   [:div.row
    (map <device> (:devices @state))]])

(defn root-component []
  [<available-devices>])

(defn detect-devices []
  (reset! state (assoc @state :devices []))
  (let [in (hardware/detect (hardware/scan))]
    (go-loop []
     (when-let [device (<! in)]
       (swap! state (fn [state device]
                      (update-in state [:devices] conj device)) device)
       (recur))))
  nil)

(reagent/render
 [root-component]
  (js/document.getElementById "application"))

(detect-devices)
