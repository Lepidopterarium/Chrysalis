;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  James Cash <james.cash@occasionallycogent.com>
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

(ns chrysalis.plugin.page.led.palette.presets
  (:require [re-frame.core :as re-frame]
            [chrysalis.settings :as settings]
            [chrysalis.plugin.page.led.palette :as palette]))

(re-frame/reg-sub
  :led.palette/presets
  (fn [db _]
    (:led.palette/presets db)))

(re-frame/reg-event-db
  :led.palette/presets.drop
  (fn [db [_ preset-name]]
    (update db :led.palette/presets dissoc preset-name)))

(re-frame/reg-event-fx
  :led.palette/presets.export
  (fn [{db :db} [_ preset-name]]
    (let [preset (get-in db [:led.palette/presets preset-name])
          device (get-in db [:device/current :meta :name])]
      {:settings/export
       {:path [:devices device :led :palette-presets preset-name]
        :value preset}})))

(re-frame/reg-event-fx
  :led.palette/presets.add
  (fn [{db :db} [_ preset-name palette]]
    (let [db (update db :led.palette/presets assoc preset-name palette)]
      {:db db
       :settings/save (settings/save! db :led)})))

(re-frame/reg-event-db
  :led.palette/presets.name
  (fn [db [_ new-name]]
    (assoc db :led.palette/current-preset-name new-name)))

(re-frame/reg-sub
  :led.palette/presets.name
  (fn [db _]
    (:led.palette/current-preset-name db)))

(defn name!
  [new-name]
  (re-frame/dispatch [:led.palette/presets.name new-name]))

(defn <save-palette>
  []
  [:div.modal.fade {:id "chrysalis-plugin-page-led-save-palette"}
   [:div.modal-dialog.modal-lg
    [:div.modal-content.bg-faded
     [:div.modal-header
      [:h5.modal-title "Save palette"]
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
             (.modal (js/$ "#chrysalis-plugin-page-led-save-palette") "hide")
             (re-frame/dispatch
               [:led.palette/presets.add
                @(re-frame/subscribe [:led.palette/presets.name])
                @(re-frame/subscribe [:led/palette])]))}
          [:div.input-group-addon {:title "Name"} [:i.fa.fa-hdd-o]]
          [:input.form-control
           {:type :text
            :value @(re-frame/subscribe [:led.palette/presets.name])
            :placeholder "Name your palette..."
            :on-change
            (fn [e]
              (re-frame/dispatch [:led.palette/presets.name
                                  (.. e -target -value)]))}]]]]]]
     [:div.modal-footer
      [:a.btn.btn-primary
       {:href "#"
        :data-dismiss :modal
        :on-click (fn [e]
                    (re-frame/dispatch
                      [:led.palette/presets.add
                       @(re-frame/subscribe [:led.palette/presets.name])
                       @(re-frame/subscribe [:led/palette])]))}
       "Save"]
      [:a.btn.btn-secondary {:href "#"
                             :data-dismiss :modal}
       "Cancel"]]]]])

(defn- <palette-theme>
  [preset-name palette]
  [:div.card {:href "#"
              :key (str "chrysalis-plugin-led-palette-preset-" preset-name)}
   [:h5.card-header preset-name]
   [:div.card-block
    [:a.card-text {:href "#"
                   :on-click (fn [e]
                               (re-frame/dispatch [:led/palette! palette]))}
     [palette/<palette> palette]]]
   [:div.card-footer.text-left
    [:span.card-text
     [:a {:href "#"
          :title "Export"
          :on-click (fn [e]
                      (.preventDefault e)
                      (re-frame/dispatch [:led.palette/presets.export
                                          preset-name]))}
      [:i.fa.fa-share]]
     [:a {:style {:float :right}
          :href "#"
          :title "Remove"
          :on-click (fn [e]
                      (.preventDefault e)
                      (re-frame/dispatch [:led.palette/presets.drop preset-name]))}
      [:i.fa.fa-minus]]]]])

(defn <presets> []
  [:div.card-group
   (doall
     (for [[name palette] @(re-frame/subscribe [:led.palette/presets])]
       ^{:key name}
       [<palette-theme> name palette]))])
