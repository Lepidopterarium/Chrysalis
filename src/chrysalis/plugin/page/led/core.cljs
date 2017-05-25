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

(ns chrysalis.plugin.page.led.core
  (:require 
            [chrysalis.ui :as ui :refer [color->hex]]
            [chrysalis.ui.page :as page]
            [chrysalis.hardware :as hardware]
            [chrysalis.device :as device]
            [chrysalis.command :as command]
            [chrysalis.command.post-process :as post-process]
            ;;[chrysalis.settings :as settings]

            [chrysalis.plugin.page.led.events :as events]
            [chrysalis.plugin.page.led.theme :as theme]
            [chrysalis.plugin.page.led.color-picker :refer [<color-picker>]]
            [chrysalis.plugin.page.led.presets :refer [<save-theme> <presets>]]

            [re-frame.core :as re-frame]
            [garden.units :as gu]
            [clojure.walk :as walk]
            [clojure.string :as s]))

(defn render []
  [:div.container-fluid {:id :led}
   [ui/style [:#page
              [:#led
               [:.key:hover {:cursor :pointer
                             :stroke-width (gu/px 3)
                             :stroke "#0000ff"}]]]]
   [<save-theme>]
   [:div.row
    [:div.col-sm-9.text-center
     [theme/<led-theme>
      (device/current)
      @(get-in (device/current) [:meta :layout])
      (events/theme)
      {:width 1024 :height 640 :interactive? true}]
     [:div.btn-toolbar.justify-content-center
      [:div.btn-group.mr-2
       [:a.btn.btn-primary {:href "#"
                            :on-click nil #_(fn [e]
                                        (let [theme (get-in @state [:led :theme])
                                              theme-str (s/join " " (flatten @theme))]
                                          (set-theme! theme-str)))}
        [:i.fa.fa-paint-brush] " Apply"]]
      [:div.btn-group.mr-2
       [:a.btn.btn-success {:href "#chrysalis-plugin-page-led-save-theme"
                            :data-toggle :modal
                            :on-click (fn [e]
                                        #_(swap! state assoc-in [:led :save-theme :name] nil))}
        [:i.fa.fa-floppy-o] " Save"]]]]
    [:div.col-sm-3.text-center.bg-faded
     [:h4 "Color picker"]
     [<color-picker>]
     [:hr]
     [:h4 "Presets"
      [:small {:style {:float :right}}
       [:a {:href "#"
            :title "Import a preset..."}
        [:i.fa.fa-plus]]]]
     [<presets>]]]])

(comment
  (swap! settings/hooks assoc :led {:save (fn []
                                           (let [device (keyword (get-in (device/current) [:device :meta :name]))]
                                             (swap! settings/data assoc-in [:devices device :led :presets]
                                                    (get-in @state [:led :presets]))))
                                   :load (fn [] nil)}))

(page/add! :led {:name "LED Theme Editor"
                 :index 10
                 :disable? (fn [] (nil? (device/current)))
                 :device/need? true
                 :render render
                 :events {:led/theme :update}})
