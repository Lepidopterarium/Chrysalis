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

(ns chrysalis.plugin.page.devices.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chrysalis.hardware :as hardware]
            [chrysalis.command :as command]
            [chrysalis.device :as device]
            [chrysalis.ui :refer [pages page state]]

            [clojure.string :as s]
            [cljs.core.async :refer [<!]]))

(defn <device> [device]
  (let [current? (and (:current-device @state)
                      (= (:comName device) (get-in @state [:current-device :device :comName])))]
    [:a.card.chrysalis-page-selector-device {:key (:comName device)
                                             :href "#"
                                             :disabled current?
                                             :class (when current? "card-outline-success")
                                             :on-click (fn [e]
                                                         (if current?
                                                           (device/close!)
                                                           (device/open! device)))}
     [:div.card-block
      [:div.card-text.text-center
       (if-let [logo-url (get-in device [:meta :logo])]
         [:img {:src logo-url}]
         [:p
          "[Image comes here]"])
       [:p.text-mute.chrysalis-link-button
        (get-in device [:meta :name])]]]]))

(defmethod page :devices [_]
  [:div.container-fluid
   [:div.row.justify-content-center
    [:div.col-12.text-center
     [:h2 "Available devices"]]]
   [:div.row.justify-content-center
    [:div.card-deck
     (doall (map <device> (:devices @state)))]]])

(swap! pages assoc :devices {:name "Devices"
                             :index 0})
