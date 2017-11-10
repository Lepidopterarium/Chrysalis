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

(ns chrysalis.plugin.page.led.presets
  (:require [chrysalis.plugin.page.led.events :as events]
            [chrysalis.plugin.page.led.theme :as theme]

            [chrysalis.device :as device]

            [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :led/presets
 (fn [db _]
   (:led/presets db)))

(re-frame/reg-event-db
 :led/presets.drop
 (fn [db [_ preset-name]]
   (update db :led/presets dissoc preset-name)))

(re-frame/reg-event-db
 :led/presets.add
 (fn [db [_ preset-name colormap]]
   (update db :led/presets assoc preset-name colormap)))

(re-frame/reg-event-db
 :led/presets.name
 (fn [db [_ new-name]]
   (assoc db :led/current-preset-name new-name)))

(re-frame/reg-sub
 :led/presets.name
 (fn [db _]
   (:led/current-preset-name db)))

(defn name! [new-name]
  (re-frame/dispatch [:led/presets.name new-name]))

(defn <save-theme> []
  [:div.modal.fade {:id "chrysalis-plugin-page-led-save-theme"}
   [:div.modal-dialog.modal-lg
    [:div.modal-content.bg-faded
     [:div.modal-header
      [:h5.modal-title "Save theme"]
      [:button.close {:type :close
                      :data-dismiss :modal}
       [:span "×"]]]
     [:div.modal-body
      [:div.container-fluid
       [:div.row
        [:div.col-sm-12
         [:form.input-group
          {:on-submit
           (fn [e]
             (.preventDefault e)
             (.modal (js/$ "#chrysalis-plugin-page-led-save-theme") "hide")
             (re-frame/dispatch [:led/presets.add
                                 @(re-frame/subscribe [:led/presets.name])
                                 @(re-frame/subscribe [:led/colormap.raw])]))}
          [:div.input-group-addon {:title "Name"} [:i.fa.fa-hdd-o]]
          [:input.form-control
           {:type :text
            :value @(re-frame/subscribe [:led/presets.name])
            :placeholder "Name your theme..."
            :on-change (fn [e]
                         (re-frame/dispatch [:led/presets.name (.-value (.-target e))]))}]]]]]]
     [:div.modal-footer
      [:a.btn.btn-primary
       {:href "#"
        :data-dismiss :modal
        :on-click (fn [e]
                    (re-frame/dispatch [:led/presets.add
                                        @(re-frame/subscribe [:led/presets.name])
                                        @(re-frame/subscribe [:led/colormap.raw])]))}
       "Save"]
      [:a.btn.btn-secondary {:href "#"
                             :data-dismiss :modal}
       "Cancel"]]]]])

(defn- <preset> [[preset-name theme]]
  [:div.card {:href "#"
              :key (str "chrysalis-plugin-led-preset-" preset-name)}
   [:h5.card-header preset-name]
   [:div.card-block
    [:a.card-text {:href "#"
                   :on-click (fn [e]
                               (re-frame/dispatch [:led/colormap.layer! theme]))}
     [theme/<led-theme>
      {:device (device/current)
      :svg @(get-in (device/current) [:meta :layout])
      :theme (mapv (events/palette) theme)
      :props {:width 102 :height 64}}]]]
   [:div.card-footer.text-left
    [:span.card-text
     [:a {:style {:float :right}
          :href "#"
          :title "Remove"
          :on-click (fn [e]
                      (.preventDefault e)
                      (re-frame/dispatch [:led/presets.drop preset-name]))}
      [:i.fa.fa-minus]]]]])

(defn <presets> []
  [:div.card-group
   (doall (map <preset> @(re-frame/subscribe [:led/presets])))])
