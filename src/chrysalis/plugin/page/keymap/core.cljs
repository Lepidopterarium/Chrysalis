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

            [garden.units :as gu]))



(defn render []
  [:div.container-fluid {:id :keymap}
   [ui/style [:#page [:#keymap]]]

   [:div.text-center
    [:h1 "Hello, world."]
    [:p1 "This is where you'll find the Keymap Editor."]
    [:div.cog [:i.fa.fa-cog.fa-5x.fa-spin]]
    ]])


(page/add! :keymap {:name "Keymap Editor"
                     :index 6
                     :render render})
