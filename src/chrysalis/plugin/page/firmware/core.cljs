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

(ns chrysalis.plugin.page.firmware.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chrysalis.ui :refer [pages page state]]
            [chrysalis.command :as command]

            [clojure.string :as s]
            [cljs.core.async :refer [<!]]))

(def dialog (.-dialog (.-remote (js/require "electron"))))

(defn basename [path]
  (when path
    (last (.split path #"/|\\"))))

(defn drop-down []
  (let [firmware-file (get-in @state [:flash :firmware-file])]
    [:span.dropdown {:class (when firmware-file
                              "show")}
     [:a.dropdown-toggle.chrysalis-link-button {:href "#"
                                                :data-toggle :dropdown}
      [:small [:i.fa.fa-cog]]]
     [:div.dropdown-menu.text-center
      [:div.input-group
       [:div.input-group-addon
        [:a.chrysalis-link-button
         {:href "#"
          :on-click (fn [e]
                      (.showOpenDialog dialog  (clj->js {"title" "Select a firmware"
                                                         "filters" [{"name" "Firmware files"
                                                                     "extensions" ["hex"]}
                                                                    {"name" "All files"
                                                                     "extensions" ["*"]}]})
                                       (fn [file-names]
                                         (swap! state assoc-in [:flash :firmware-file] (first file-names)))))}
         [:i.fa.fa-code]]]
       [:input.form-control {:type :text
                             :disabled :true
                             :placeholder "Please select a file"
                             :value (or (basename firmware-file) "")}]
       [:div.input-group-addon
        [:a.chrysalis-link-button {:href "#"
                                   :on-click (fn []
                                               (swap! state assoc-in [:flash :firmware-file] nil))}
         [:i.fa.fa-eraser]]]]
      (when firmware-file
        [:a.btn.btn-primary {:href "#"}
         "Upload"])]]))

(defn firmware-version []
  (let [version (command/run (get-in @state [:current-device :port]) :version)]
    (fn []
      [:div.card-text
       [:div.text-muted
        [:code (:firmware-version @version)]]
       [:div.text-muted
        "Built on " [:i (:timestamp @version)]
        [drop-down]]])))

(defmethod page :firmware [_]
  [:div.container-fluid
   [:div.row.justify-content-center
    [:div.col-sm-12.text-center
     [:h2 "Flash a new firmware"]]]

   [:div.row.justify-content-center
    [:div.card.chrysalis-page-firmware-card
     [:img.card-img-top {:alt "Kaleidoscope Logo"
                         :src "images/kaleidoscope-logo-ph-small.png"}]
     [:div.card-block
      [:h4.card-title "Kaleidoscope"]
      [firmware-version]]]]])

(swap! pages assoc :firmware {:name "Firmware"
                              :index 80
                              :disable? (fn [] (nil? (get-in @state [:current-device :port])))})
