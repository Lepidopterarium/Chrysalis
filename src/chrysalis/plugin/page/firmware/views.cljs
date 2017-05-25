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

(ns chrysalis.plugin.page.firmware.views
  (:require [chrysalis.ui :as ui]
            [chrysalis.plugin.page.firmware.events :as events]

            [garden.units :as gu]))

(defonce dialog (.-dialog (.-remote (js/require "electron"))))

(defn- basename [path]
  (when path
    (last (.split path #"/|\\"))))

(defn drop-down []
  (let [hex-file (events/hex-file)]
    [:span.dropdown {:class (when hex-file
                              "show")}
     [:a.dropdown-toggle.link-button {:href "#"
                                      :data-toggle :dropdown}
      [:small [:i.fa.fa-cog]]]
     [:div.dropdown-menu.text-center
      [:div.input-group
       [:div.input-group-addon
        [:a.link-button
         {:href "#"
          :on-click (fn [e]
                      (events/state! :default)
                      (.showOpenDialog dialog  (clj->js {"title" "Select a firmware"
                                                         "filters" [{"name" "Firmware files"
                                                                     "extensions" ["hex"]}
                                                                    {"name" "All files"
                                                                     "extensions" ["*"]}]})
                                       (fn [file-names]
                                         (events/hex-file! (first file-names)))))}
         [:i.fa.fa-code]]]
       [:input.form-control {:type :text
                             :disabled :true
                             :placeholder "Please select a file"
                             :title hex-file
                             :value (or (basename hex-file) "")}]
       [:div.input-group-addon
        [:a.link-button {:href "#"
                         :on-click (fn []
                                     (events/state! :default)
                                     (events/hex-file! nil))}
         [:i.fa.fa-eraser]]]]
      (when hex-file
        [:a.btn.btn-primary {:href "#"
                             :on-click events/upload!}
         "Upload"])]]))

(defn render []
  (let [version (events/version)
        firmware-state (events/state)]
    [:div.container-fluid {:id :firmware}
     [ui/style [:#page
                [:#firmware
                 [:.card {:min-width (gu/px 375)}
                  [:.dropdown {:float :right}
                   [:.dropdown-menu {:padding-left (gu/em 0.25)
                                     :padding-right (gu/em 0.25)
                                     :left (gu/em -22)}]]
                  ["input[type=text]" {:padding-left (gu/em 0.5)
                                       :width (gu/em 20)}]
                  [:a.btn {:margin-top (gu/em 0.5)}]]]]]
     [:div.row.justify-content-center
      [:div.card {:class (condp = firmware-state
                           :uploading "card-outline-info"
                           :success "card-outline-success"
                           :error "card-outline-danger"
                           nil)}
       [:img.card-img-top {:alt "Kaleidoscope Logo"
                           :class (when (:uploading #{firmware-state})
                                    "fa-spin")
                           :src "images/kaleidoscope-logo-ph-small.png"}]
       [:div.card-block
        [:h4.card-title {:class (condp = firmware-state
                                  :uploading "text-info"
                                  :success "text-success"
                                  :error "text-danger"
                                  nil)}
         "Kaleidoscope"]
        [:div.card-text
         [:div.text-muted
          [:code (:firmware-version version)]]
         [:div.text-muted
          "Built on " [:i (:timestamp version)]
          [drop-down]]]]]]]))
