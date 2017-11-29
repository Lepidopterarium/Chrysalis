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
            [chrysalis.plugin.page.led.palette :as palette]
            [chrysalis.plugin.page.led.color-picker :as color-picker]
            [chrysalis.plugin.page.led.palette.presets :as palette-presets]
            [chrysalis.plugin.page.led.presets :as presets]

            [garden.units :as gu]))

(defn <live-update> []
  [:form.form-group.form-check
   [:label.form-check-label
    [:input.form-check-input
     {:type :checkbox
      :value (events/live-update?)
      :on-change (fn [e]
                   (events/live-update! (.. e -target -checked)))}]
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
   [palette-presets/<save-palette>]
   [:div.row
    [:div.col-sm-9.text-center
     [theme/<led-theme>
      {:device (device/current)
      :svg @(get-in (device/current) [:meta :layout])
      :theme (events/colormap)
      :props {:interactive? true}}]]
    [:div.col-sm-3.text-center.justify-content-center.bg-faded

     [<live-update>]
     (when-not (events/live-update?)
       [:div.form-group.form-check
        [:button.btn.btn-primary
         {:on-click (fn [_]
                      (events/palette:upload!)
                      (events/colormap:upload!))}
         [:i.fa.fa-paint-brush] " Apply"]
        [:button.btn.btn-secondary
         {:on-click (fn [_]
                      (events/palette:update!)
                      (events/colormap:update!))}
         "Cancel"]])
     [:hr]
     [:label.mr-sm-2 "Layer"
      [:select.custom-select
       {:value (events/layer)
        :on-change (fn [e]
                     (events/switch-layer (js/parseInt (-> e .-target .-value) 10)))}
       ;; TODO: is there a way to check how many layers there are?
       [:option {:value 1} "1"]
       [:option {:value 2} "2"]
       [:option {:value 3} "3"]
       [:option {:value 4} "4"]
       [:option {:value 5} "5"]]]]]

   [:div.row
    [:div.col-sm-6.text-left
     [:h4 "Color Palette"]
     [palette/<palette> (events/palette)]]
    [:div.col-sm-6.text-center
     [:h4 "Adjust Palette Colour"]
     [color-picker/<color-picker>]]]

   [:div.row
    [:button.btn-primary
     {:on-click (fn [e] (.preventDefault e)
                  (events/import-presets!))
      :title "Import a preset..."}
     [:i.fa.fa-plus] "Import Presets"]]

   [:div.row
    [:div.col-sm-12.text-center
     [:h4 "Colormap Presets"]
     [:div.btn-toolbar.justify-content-center
      [:div.btn-group.mr-2
       [:a.btn.btn-success {:href "#chrysalis-plugin-page-led-save-theme"
                            :data-toggle :modal
                            :on-click (fn [e]
                                        (presets/name! nil))}
        [:i.fa.fa-floppy-o] " Save"]]]
     [presets/<presets>]]]

   [:div.row
    [:div.col-sm-12.tex-center
     [:h4 "Palette Presets"]
     [:div.btn-toolbar.justify-content-center
      [:div.btn-group.mr-2
       [:a.btn.btn-success {:href "#chrysalis-plugin-page-led-save-palette"
                            :data-toggle :modal
                            :on-click (fn [e]
                                        (palette-presets/name! nil))}
        [:i.fa.fa-floppy-o] " Save"]]]
     [palette-presets/<presets>]]]])

(defmethod settings/apply! [:led] [db _]
  (let [device-name (get-in (device/current) [:meta :name])]
    (-> db
        (settings/copy->
          [:devices device-name :led :presets]
          [:led/presets])
        (settings/copy->
          [:devices device-name :led :palette-presets]
          [:led.palette/presets]))))

(defmethod settings/save! [:led] [db _]
  (let [device-name (get-in (device/current) [:meta :name])]
    (merge
      (settings/<-copy
        db
        [:devices device-name :led :presets]
        [:led/presets])
      (settings/<-copy
        db
        [:devices device-name :led :palette-presets]
        [:led.palette/presets]))))

(page/add! :led {:name "LED Theme Editor"
                 :index 10
                 :disable? (fn [] (nil? (device/current)))
                 :device/need? true
                 :render render
                 :events {:led/colormap :update
                          :led/palette :update}})
