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
  (:require [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.ui.page :as page]
            [chrysalis.key-bindings :as key-bindings]

            [garden.units :as gu]))

(defn <device> [device index]
  (when device
    (let [current? (= (:comName device) (:comName (device/current)))]
      (key-bindings/add! (str "ctrl+" index)
                         (fn []
                           (if current?
                             (device/select! nil)
                             (device/select! device))))
      [:a.card.device {:key (:comName device)
                       :href "#"
                       :disabled current?
                       :class (when current? "card-outline-success")
                       :on-click (fn [e]
                                   (if current?
                                     (device/select! nil)
                                     (device/select! device)))}
       [:div.card-block
        [:div.card-text.text-center
         (if-let [logo-url (get-in device [:meta :logo])]
           [:img {:src logo-url}]
           [:p
            "[Image comes here]"])
         [:p.text-mute.link-button
          (get-in device [:meta :name])]]]
       [:div.card-footer
        [:div.row
         [:small.col-sm-6.text-muted
          (:comName device)]
         [:small.col-sm-6.text-right.text-muted
          "Ctrl+" index]]]])))

(defn render []
  [:div.container-fluid {:id "devices"}
   [ui/style [:#page [:#devices
                      [:.device {:margin (gu/em 0.5)
                                 :min-width (gu/px 350)}]
                      [:.device:hover :device:focus {:text-decoration :none}]
                      [:.device:hover {:border-color "#5bc0de"}]]]]
   [:div.row.justify-content-center
    [:div.card-deck
     (doall (map <device>
                 (device/list)
                 (range)))]]])

(page/add! :devices {:name "Device Selector"
                     :index 0
                     :render render})
