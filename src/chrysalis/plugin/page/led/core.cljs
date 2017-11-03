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
  (:require [chrysalis.ui :as ui]
            [chrysalis.ui.page :as page]
            [chrysalis.device :as device]
            [chrysalis.settings :as settings]

            [chrysalis.plugin.page.led.events :as events]
            [chrysalis.plugin.page.led.theme :as theme]
            [chrysalis.plugin.page.led.color-picker :as color-picker]
            [chrysalis.plugin.page.led.presets :as presets]

            [garden.units :as gu]))

(defn <live-update> []
  [:form.form-group.form-check
   [:label.form-check-label
    [:input.form-check-input {:type :checkbox
                              :checked (events/live-update?)
                              :on-change (fn [e]
                                           (events/live-update! (-> e .-target .-checked)))}]
    " Live update"]])

(defn render []
  [:div.container-fluid {:id :led}
   [ui/style [:#page
              [:#led
               [:.chrome-picker {:display :inline-block
                                 :margin-bottom (gu/em 1)}]
               [:.key:hover {:cursor :pointer
                             :stroke-width (gu/px 2)
                             :stroke "#0000ff"}]]]]
   [presets/<save-theme>]
   [:div.row
    [:div.col-sm-9.text-center
     [theme/<led-theme>
      (device/current)
      @(get-in (device/current) [:meta :layout])
      (events/theme)
      {:max-height "50vh" :interactive? true}]
     [:div.btn-toolbar.justify-content-center
      [:div.btn-group.mr-2
       [:a.btn.btn-primary {:href "#"
                            :on-click (fn [e]
                                        (events/theme:upload!))}
        [:i.fa.fa-paint-brush] " Apply"]]
      [:div.btn-group.mr-2
       [:a.btn.btn-success {:href "#chrysalis-plugin-page-led-save-theme"
                            :data-toggle :modal
                            :on-click (fn [e]
                                        (presets/name! nil))}
        [:i.fa.fa-floppy-o] " Save"]]]]
    [:div.col-sm-3.text-center.justify-content-center.bg-faded
     [<live-update>]
     [:h4 "Color picker"]
     [color-picker/<color-picker>]
     [:hr]
     [:h4 "Presets"
      [:small {:style {:float :right}}
       [:a {:href "#"
            :title "Import a preset..."}
        [:i.fa.fa-plus]]]]
     [presets/<presets>]]]])

(defmethod settings/apply! [:led] [db _]
  (settings/copy-> db
                   [:devices (keyword (get-in (device/current) [:meta :name])) :led :presets]
                   [:led/presets]))

(defmethod settings/save! [:led] [db _]
  (settings/<-copy db
                   [:devices (keyword (get-in (device/current) [:meta :name])) :led :presets]
                   [:led/presets]))

(page/add! :led {:name "LED Theme Editor"
                 :index 10
                 :disable? (fn [] (nil? (device/current)))
                 :device/need? true
                 :render render
                 :events {:led/theme :update}})
