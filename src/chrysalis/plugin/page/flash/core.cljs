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

(ns chrysalis.plugin.page.flash.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chrysalis.ui :refer [pages page state]]

            [clojure.string :as s]
            [cljs.core.async :refer [<!]]))

(def dialog (.-dialog (.-remote (js/require "electron"))))

(defmethod page :flash [_]
  [:div.container-fluid
   [:div.row.justify-content-center
    [:div.col-12.text-center
     [:h2 "Flash a new firmware"]]]
   [:div.row
    [:div.col-sm-12.text-center
     [:button {:type :button
               :on-click (fn [e]
                           (.showOpenDialog dialog  (clj->js {"title" "Select a firmware",
                                                              "filters" [{"name" "Firmware files",
                                                                          "extensions" ["hex"]}]})))}
      "Choose a firmware"]]]])

(swap! pages assoc :flash {:name "Flash"
                           :index 1
                           :disable? (fn [] (nil? (get-in @state [:current-device :port])))})
