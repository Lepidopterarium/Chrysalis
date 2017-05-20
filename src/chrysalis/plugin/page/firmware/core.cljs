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
  (:require [chrysalis.core :refer [state pages]]
            [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.ui.page :refer [page]]
            [chrysalis.command :as command]
            [chrysalis.hardware :as hardware]
            [chrysalis.settings :as settings]

            [garden.units :as gu]))

(def dialog (.-dialog (.-remote (js/require "electron"))))
(def Avrgirl (js/require "avrgirl-arduino"))

(defn basename [path]
  (when path
    (last (.split path #"/|\\"))))

(defn firmware-state! [s]
  (swap! state assoc-in [:firmware :state] s))

(defn- get-firmware-version []
  (let [port (hardware/open (get-in (device/current) [:device :comName]))
        version (command/run port :version)]
    (.setTimeout js/window #(.close port) 100)
    (swap! state assoc-in [:firmware :version] version)))

(defn upload [hex-name]
  (let [avrgirl (Avrgirl. (clj->js {"board" (get-in @state [:current-device :device :board])
                                    "debug" true}))
        device (device/current)]
    (firmware-state! :uploading)
    (device/switch-to! nil)
    (.flash avrgirl hex-name (fn [error]
                               (when error
                                 (.error js/console error))
                               (.setTimeout js/window
                                            (fn []
                                              (device/switch-to! nil)
                                              (device/detect!)

                                              (.setTimeout js/window
                                                           (fn []
                                                             (device/select-by-serial! (get-in device [:device :serialNumber]))
                                                             (get-firmware-version)
                                                             (if error
                                                               (firmware-state! :error)
                                                               (firmware-state! :success)))
                                                           1000))
                                            2000)))))

(defn drop-down []
  (let [hex-file (get-in @state [:firmware :hex-file])]
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
        [:a.link-button {:href "#"
                         :on-click (fn []
                                     (firmware-state! :default)
                                     (swap! state assoc-in [:firmware :hex-file] nil))}
         [:i.fa.fa-eraser]]]]
      (when hex-file
        [:a.btn.btn-primary {:href "#"
                             :on-click #(upload hex-file)}
         "Upload"])]]))

(defmethod page [:enter :firmware] [_ _]
  (get-firmware-version)
  (let [device (keyword (get-in (device/current) [:device :meta :name]))]
    (swap! state assoc-in [:firmware :hex-file]
           (get-in @settings/data [:devices device :firmware :latest-hex]))))

(defmethod page [:leave :firmware] [_ _]
  (settings/save! :firmware)
  (swap! state assoc-in [:firmware :state] :default))

(defmethod page [:render :firmware] [_ _]
  (let [version (get-in @state [:firmware :version])]
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
      [:div.card {:class (condp = (get-in @state [:firmware :state])
                           :uploading "card-outline-info"
                           :success "card-outline-success"
                           :error "card-outline-danger"
                           nil)}
       [:img.card-img-top {:alt "Kaleidoscope Logo"
                           :class (when (:uploading #{(get-in @state [:firmware :state])})
                                    "fa-spin")
                           :src "images/kaleidoscope-logo-ph-small.png"}]
       [:div.card-block
        [:h4.card-title {:class (condp = (get-in @state [:firmware :state])
                                  :uploading "text-info"
                                  :success "text-success"
                                  :error "text-danger"
                                  nil)}
         "Kaleidoscope"]
        [:div.card-text
         [:div.text-muted
          [:code (:firmware-version @version)]]
         [:div.text-muted
          "Built on " [:i (:timestamp @version)]
          [drop-down]]]]]]]))

(swap! state assoc :firmware {:state :default})
(swap! pages assoc :firmware {:name "Firmware Flasher"
                              :index 80
                              :disable? (fn [] (nil? (device/current)))})

(swap! settings/hooks assoc :firmware {:save (fn []
                                               (let [device (keyword (get-in (device/current) [:device :meta :name]))]
                                                 (swap! settings/data assoc-in [:devices device :firmware :latest-hex]
                                                        (get-in @state [:firmware :hex-file]))))
                                       :load (fn [] nil)})
