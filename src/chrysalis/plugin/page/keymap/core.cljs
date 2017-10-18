;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  Simon-Claudius Wystrach <mail@simonclaudius.com>
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

(ns chrysalis.plugin.page.keymap.core
  (:require [clojure.string :as s]
            [reagent.core :as r]
            [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.ui.page :as page]
            [chrysalis.key-bindings :as key-bindings]

            [chrysalis.plugin.page.keymap.events :as events]
            [chrysalis.plugin.page.keymap.layout :as layout]

            [garden.units :as gu]
            [chrysalis.key :as key]
            [re-frame.core :as re-frame]))

(defn <live-update> []
  [:form.form-group.form-check
   [:label.form-check-label
    [:input.form-check-input {:type :checkbox
                              :value (events/live-update?)
                              :on-change (fn [e]
                                           (events/live-update! (.. e -target -checked)))}]
    " Live update"]])

(defn- key-name
  [key]
  (str (when (and (:key key) (s/starts-with? (name (:key key)) "keypad_"))
         "Keypad ")
       (:primary-text (key/format key))))

(defn edit-key-view
  [{:keys [index key layer]}]
  [:div.edit-key
   [:dl
    [:dt "Key Code"]
    [:dd
     [:select.custom-select
      {:value (:key key "")
       :on-change (fn [e] (let [new-key (keyword (.. e -target -value))]
                           (events/change-key! (dec layer) index
                                               {:plugin :core
                                                :key new-key
                                                :modifiers []})))}
      (doall
        (for [k (remove nil? key/HID-Codes)]
          ^{:key k}
          [:option {:value (:key k)} (key-name k)]))]]
    [:dt "Modifiers"]
    [:dd (if-let [mods (seq (:modifiers key))]
           (s/join mods ", ")
           "None")]]])

(defn render []
  [:div.container-fluid {:id :keymap}
   [ui/style [:#page [:#keymap
                      [:.key:hover {:cursor :pointer
                                    :stroke-width (gu/px 2)
                                    :stroke "#000000"}]]]]

   [:div.row
    [:div.col-sm-6.text-center
     ;; TODO: only show this once, or in a more unobtrusive way
     ;; TODO: can we check if they have the eeprom plugin enabled &
     ;; only show if they haven't?
     [:p.warning
      "To change the keymap from here, you'll need to have installed the "
      [:a {:href "https://github.com/keyboardio/Kaleidoscope-EEPROM-Keymap"}
       "Keymap-EEPROM plugin"] "."]

     [layout/<keymap-layout>
      (device/current)
      @(get-in (device/current) [:meta :layout])
      (events/layout)
      {:width 1024 :height 640 :interactive? true}]]
    [:div.col-sm-3.text-center
     (when-let [cur-key (events/current-target)]
       (let [[r c] (->> ["data-row" "data-column"]
                       (map (comp #(js/parseInt % 10) #(.getAttribute cur-key %))))
             index (js/parseInt (.getAttribute cur-key "data-index") 10)
             key (get-in (events/layout) [(dec (events/layer)) index])]
         [edit-key-view {:key key :index index :layer (events/layer)}]))]
    [:div.col-sm-3.text-center
     [<live-update>]
     [:label.mr-sm-2 "Layer"]
     [:select.custom-select {:value (events/layer)
                             :on-change (fn [e]
                                          (events/switch-layer (-> e .-target .-value)))}
      ;; TODO: is there a way to check how many layers there are?
      [:option {:value 1} "1"]
      [:option {:value 2} "2"]
      [:option {:value 3} "3"]
      [:option {:value 4} "4"]
      [:option {:value 5} "5"]]]]])


(page/add! :keymap {:name "Keymap Editor"
                    :index 6
                    :disable? (fn [] (nil? (device/current)))
                    :device/need? true
                    :render render
                    :events {:keymap/layout :update}})
