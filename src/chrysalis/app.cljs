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
  (:require [reagent.core :as reagent]
            [clojure.string :as s]

            [chrysalis.utils :refer [state]]
            [chrysalis.ui.page :refer [pages page]]

            ;; Hook-only libraries

            [chrysalis.ui.page.selector :as selector]
            [chrysalis.ui.page.repl]

            [chrysalis.ui.FingerPainter]

            [chrysalis.command.LEDControl]

            [chrysalis.hardware.fake]
            [chrysalis.hardware.model01]
            [chrysalis.hardware.shortcut]))

(enable-console-print!)

(defn <menu-item> [[key meta]]
  [:li {:key (str "main-menu-" (name key))
        :class (s/join " " ["nav-item"
                            (when (= key (:page @state))
                              "active")])}
   [:a.nav-link {:href "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (swap! state assoc :page key))}
    (:name meta)]])

(defn <main-menu> []
  [:nav.navbar.navbar-toggleable-md.navbar-light.bg-faded
   [:button.navbar-toggler.navbar-toggler-right {:type :button
                                                 :data-toggle :collapse
                                                 :data-target "#navbarSupportedContent"
                                                 :aria-controls "navbarSupportedContent"
                                                 :aria-expanded false
                                                 :aria-label "Toggle navigation"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand {:href "#"} "Chrysalis"]
   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}
    [:ul.navbar-nav.mr-auto
     (doall (map <menu-item> @pages))]]])

(defn root-component []
  [:div
   [<main-menu>]
   (page (:page @state))])

(defn init! []
  (selector/device-detect!))

(reagent/render
 [root-component]
  (js/document.getElementById "application"))

(init!)
