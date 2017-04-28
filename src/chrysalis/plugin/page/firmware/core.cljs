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
            [chrysalis.hardware :as hardware]

            [clojure.string :as s]
            [cljs.core.async :refer [<!]]))

(def dialog (.-dialog (.-remote (js/require "electron"))))
(def Avrgirl (js/require "avrgirl-arduino"))

(defn basename [path]
  (when path
    (last (.split path #"/|\\"))))

(defn device-reopen [device]
  (swap! state assoc :current-device {:port (hardware/open (:comName device))
                                      :device device}))

(defn upload [hex-name]
  (let [avrgirl (Avrgirl. (clj->js {"board" (get-in @state [:current-device :device :board])}))
        device (get-in @state [:current-device :device])
        port (get-in @state [:current-device :port])]
    (swap! state assoc-in [:firmware :state] :uploading)
    (.close port
            (fn [_]
              (.flash avrgirl hex-name (fn [error]
                                         (if error
                                           (do
                                             (swap! state assoc-in [:firmware :state] :error)
                                             (.log js/console error)
                                             )
                                           (do
                                             (swap! state assoc-in [:firmware :state] :success)
                                             (device-reopen device)))))))))

(defn drop-down []
  (let [hex-file (get-in @state [:firmware :hex-file])]
    [:span.dropdown {:class (when hex-file
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
                      (swap! state assoc-in [:firmware :state] :default)
                      (.showOpenDialog dialog  (clj->js {"title" "Select a firmware"
                                                         "filters" [{"name" "Firmware files"
                                                                     "extensions" ["hex"]}
                                                                    {"name" "All files"
                                                                     "extensions" ["*"]}]})
                                       (fn [file-names]
                                         (swap! state assoc-in [:firmware :hex-file] (first file-names)))))}
         [:i.fa.fa-code]]]
       [:input.form-control {:type :text
                             :disabled :true
                             :placeholder "Please select a file"
                             :title hex-file
                             :value (or (basename hex-file) "")}]
       [:div.input-group-addon
        [:a.chrysalis-link-button {:href "#"
                                   :on-click (fn []
                                               (swap! state assoc-in [:firmware :state] :default)
                                               (swap! state assoc-in [:firmware :hex-file] nil))}
         [:i.fa.fa-eraser]]]]
      (when hex-file
        [:a.btn.btn-primary {:href "#"
                             :on-click #(upload hex-file)}
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
    [:div.card.chrysalis-page-firmware-card {:class (condp = (get-in @state [:firmware :state])
                                                      :success "card-outline-success"
                                                      :error "card-outline-danger"
                                                      nil)}
     [:img.card-img-top {:alt "Kaleidoscope Logo"
                         :class (when (:uploading #{(get-in @state [:firmware :state])})
                                  "fa-spin")
                         :src "images/kaleidoscope-logo-ph-small.png"}]
     [:div.card-block
      [:h4.card-title {:class (condp = (get-in @state [:firmware :state])
                                :success "text-success"
                                :error "text-danger"
                                nil)}
       "Kaleidoscope"]
      [firmware-version]]]]])

(swap! state assoc :firmware {:state :normal})
(swap! pages assoc :firmware {:name "Firmware"
                              :index 80
                              :disable? (fn [] (nil? (get-in @state [:current-device :port])))})
