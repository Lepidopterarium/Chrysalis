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
            [reagent.ratom :refer-macros [reaction]]
            [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.ui.page :as page]
            [chrysalis.key-bindings :as key-bindings]

            [chrysalis.plugin.page.keymap.events :as events]
            [chrysalis.plugin.page.keymap.layout :as layout]
            [chrysalis.plugin.page.keymap.presets :as presets]

            [garden.units :as gu]
            [chrysalis.key :as key]
            [chrysalis.settings :as settings]
            [chrysalis.plugin.Kaleidoscope.OneShot.keymap]))

(defn <live-update> []
  [:form.form-group.form-check
   [:label.form-check-label
    [:input.form-check-input
     {:type :checkbox
      :value (events/live-update?)
      :on-change (fn [e]
                   (events/live-update! (.. e -target -checked)))}]
    " Live update"]])

(defn- key-name
  [key]
  (let [label (:primary-text (key/format key))]
    (str (when (and (:key key)
                    (s/starts-with? (name (:key key)) "keypad_")
                    (not (s/starts-with? label "Keypad")))
           "Keypad ")
         label)))

(defn display-key
  "Display the key + mods in a nice way"
  [key]
  (reduce
    (fn [s mod]
      (case mod
        :control (str "CTRL(" s ")")
        :shift (str "SHIFT(" s ")")
        :gui (str "GUI(" s ")")
        :left-alt (str "ALT(" s ")")
        :right-alt (str "RALT(" s ")")))
    (key-name key)
    (:modifiers key)))

(defn vindex-of
  "Index-of for cljs vector"
  [v x]
  (some (fn [[idx item]] (when (= item x) idx))
        (map-indexed vector v)))

(defn- edit-tab-view
  [tab {:keys [layer index]}]
  (let [key (events/layout-key layer index)
        ;; Always have NoKey & Trans as options
        tab-keys (into [{:plugin :core :key nil}
                        {:plugin :core :key :transparent}]
                       (:keys tab))
        key-idx (or (vindex-of tab-keys (dissoc key :modifiers))
                    0)]
    [:div
     [:select.custom-select
      {:value key-idx
       :on-change (fn [e] (let [idx (js/parseInt (.. e -target -value) 10)
                               new-key (get tab-keys idx)]
                           ;; Should changing the key preserve the existing modifiers?
                           (events/change-key! layer index new-key)))}
      (doall
        (for [[i k] (map-indexed vector tab-keys)]
          ^{:key k}
          [:option {:value i} (key-name k)]))]
     (when (:modifiers? tab)
       (doall
         (for [modifier [:shift :control :gui :left-alt :right-alt]]
           ^{:key modifier}
           [:label.form-check-label
            [:input.form-check-input
             {:type :checkbox
              :disabled (or (nil? (:key key))
                            (= :transparent (:key key)))
              :checked (contains? (:modifiers key) modifier)
              :on-change
              (fn [e]
                (let [f (if (.. e -target -checked) conj disj)]
                  (events/change-key!
                    layer index
                    (update key :modifiers (fnil f #{}) modifier))))}]

            (name modifier)])))]))

(defn edit-tabs-view
  [args]
  (let [current-tab-idx (r/atom 0)
        tabs (events/edit-tabs)
        cur-tab (reaction (get tabs @current-tab-idx))]
    (fn [{:keys [index layer] :as args}]
      [:div.edit-controls
       [:ul.nav.nav-tabs
        (doall
          (for [[idx tab] (map-indexed vector tabs)]
            ^{:key [idx (:title tab)]}
            [:li
             [:a.nav-link
              {:href "#"
               :class (when (= idx @current-tab-idx) "active")
               :on-click (fn [e]
                           (.preventDefault e)
                           (reset! current-tab-idx idx))}
              (:title tab)]]))]
       [edit-tab-view @cur-tab args]])))

(defn selected-key-view
  [{:keys [index key layer] :as args}]
  [:div.edit-key
   [:dl
    [:dt "Current Key"]
    [:dd (display-key (events/saved-layout-key layer index))]
    [:dt "Edit Key"]
    [:dd [edit-tabs-view args]]]])

(defn render []
  [:div.container-fluid {:id :keymap}
   [ui/style [:#page [:#keymap
                      [:.key:hover {:cursor :pointer
                                    :stroke-width (gu/px 2)
                                    :stroke "#000000"}]]]]

   [:div.row
    [:div.col-sm-12.text-center
     [:p.bg-warning
      ;; TODO: only show this once, or in a more unobtrusive way
      ;; TODO: can we check if they have the eeprom plugin enabled &
      ;; only show if they haven't?
      "To change the keymap from here, you'll need to have installed the "
      [:a {:href "https://github.com/keyboardio/Kaleidoscope-EEPROM-Keymap"}
       "Keymap-EEPROM plugin"] "."]]]

   [:div.row
    [:div.col-sm-9.text-center

     [layout/<keymap-layout>
      {:device (device/current)
       :svg @(get-in (device/current) [:meta :layout])
       :layout (events/layout)
       :layer (dec (events/layer))
       :props {:interactive? true
               :style {:max-height "50vh"}}}]]

    [:div.col-sm-3.text-center
     [<live-update>]
     (when-not (events/live-update?)
       [:div.form-group.form-check
        [:button.btn.btn-success
         {:on-click (fn [_] (events/layout:upload!))}
         [:i.fa.fa-floppy-o] " Update"]
        [:button.btn.btn-secondary
         {:on-click (fn [_] (events/layout:update!))}
         "Cancel"]
        #_[:button.btn.btn-secondary
           {:on-click (fn [_] (events/layout:reset!))}
           "Reset To Default"]])

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
    [:div.col-sm-12.text-center
     (when-let [cur-key (events/current-target)]
       (let [[r c] (->> ["data-row" "data-column"]
                       (map (comp #(js/parseInt % 10) #(.getAttribute cur-key %))))
             index (js/parseInt (.getAttribute cur-key "data-index") 10)]
         [selected-key-view {:index index :layer (dec (events/layer))}]))]]

   [:div.row
    [:div.col-sm-12.text-center
     [:h2 "Presets"]
     [presets/<save-layout>]
     [:div.btn-group.mr-2
      [:a.btn.btn-success {:href "#chrysalis-plugin-page-keymap-save-layout"
                           :data-toggle :modal
                           :on-click (fn [e]
                                       (presets/name! nil))}
       [:i.fa.fa-floppy-o] " Save"]]
     [presets/<presets>]]]])

(defmethod settings/apply! [:keymap] [db _]
  (settings/copy-> db
                   [:devices
                    (get-in (device/current) [:meta :name])
                    :keymap :presets]
                   [:keymap/presets]))

(defmethod settings/save! [:keymap] [db _]
  (settings/<-copy db
                   [:devices
                    (get-in (device/current) [:meta :name])
                    :keymap :presets]
                   [:keymap/presets]))

(page/add! :keymap {:name "Keymap Editor"
                    :index 6
                    :disable? (fn [] (nil? (device/current)))
                    :device/need? true
                    :render render
                    :events {:keymap/layout 0}})
