;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  Simon-Claudius Wystrach <mail@simonclaudius.com>
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

(ns chrysalis.plugin.page.keymap.core
  (:require [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.ui.page :as page]
            [chrysalis.key-bindings :as key-bindings]

            [chrysalis.plugin.page.keymap.events :as events]
            [chrysalis.plugin.page.keymap.layout :as layout]

            [garden.units :as gu]))



(defn render []
  [:div.container-fluid {:id :keymap}
   [ui/style [:#page [:#keymap
                      [:.key:hover {:cursor :pointer
                                    :stroke-width (gu/px 2)
                                    :stroke "#000000"}]]]]

   [:div.row
    [:div.col-sm-9.text-center

     [:h2 "Keymap"]
     [layout/<keymap-layout>
      (device/current)
      @(get-in (device/current) [:meta :layout])
      (events/layout)
      {:width 1024 :height 640 :interactive? true}]
     ]
    [:div.col-sm-3.text-center

     [:h2 "Inspector"]]]
   ])


(page/add! :keymap {:name "Keymap Editor"
                    :index 6
                    :disable? (fn [] (nil? (device/current)))
                    :device/need? true
                    :render render
                    :events {:keymap/layout :update}})
