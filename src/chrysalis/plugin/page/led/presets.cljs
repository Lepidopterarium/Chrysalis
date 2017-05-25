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
            [chrysalis.plugin.page.led.theme :as theme]))

(comment
(defn preset [[name theme]]
  [:div.card {:href "#"
              :key (str "chrysalis-plugin-led-preset-" name)
              :on-click (fn [e]
                          (let [main-theme (get-in @state [:led :theme])]
                            (reset! main-theme theme)))}
   [:h5.card-header name]
   [:div.card-block
    [:a.card-text {:href "#"
                   :on-click (fn [e]
                               (let [main-theme (get-in @state [:led :theme])]
                                 (reset! main-theme theme)))}
     [theme/<led-theme> (get-in (device/current) [:device :meta :layout])
      (atom theme)
      {:width 102 :height 64}]]]
   [:div.card-footer.text-left
    [:span.card-text
     [:a {:href "#"}
      "Export"]]
    [:span.card-text
     [:a {:style {:float :right}
          :href "#"
          :title "Remove"
          :on-click (fn [e]
                      (let [presets (get-in @state [:led :presets])]
                        (swap! state assoc-in [:led :presets] (dissoc presets name))))}
     [:i.fa.fa-minus]]]]])

(defn presets []
  [:div.card-group
   (doall (map preset (get-in @state [:led :presets])))])

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
         [:form.input-group {:on-submit (fn [e]
                                          (.preventDefault e)
                                          (.modal (js/$ "#chrysalis-plugin-page-led-save-theme") "hide")
                                          (let [name (get-in @state [:led :save-theme :name])]
                                            (swap! state assoc-in [:led :presets name] @(get-in @state [:led :theme]))))}
          [:div.input-group-addon {:title "Name"} [:i.fa.fa-hdd-o]]
          [:input.form-control {:type :text
                                :placeholder "Name your theme..."
                                :on-change (fn [e]
                                             (swap! state assoc-in [:led :save-theme :name] (.-value (.-target e))))}]]]]]]
     [:div.modal-footer
      [:a.btn.btn-primary {:href "#"
                           :data-dismiss :modal
                           :on-click (fn [e]
                                       (let [name (get-in @state [:led :save-theme :name])]
                                         (swap! state assoc-in [:led :presets name] @(get-in @state [:led :theme]))))}
       "Save"]
      [:a.btn.btn-secondary {:href "#"
                             :data-dismiss :modal}
       "Cancel"]]]]])




)

(defn <save-theme> []
  nil)

(defn <presets> []
  nil)
