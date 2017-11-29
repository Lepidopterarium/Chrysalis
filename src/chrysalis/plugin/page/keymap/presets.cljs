(ns chrysalis.plugin.page.keymap.presets
  (:require [chrysalis.plugin.page.keymap.events :as events]
            [chrysalis.plugin.page.keymap.layout :as layout]

            [chrysalis.device :as device]
            [chrysalis.settings :as settings]

            [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :keymap/presets
 (fn [db _]
   (:keymap/presets db)))

(re-frame/reg-event-fx
 :keymap/presets.drop
 (fn [{db :db} [_ preset-name]]
   (let [db (update db :keymap/presets dissoc preset-name)
         settings (settings/save! db :keymap)]
     {:db (assoc db :settings settings)
      :settings/save settings})))

(re-frame/reg-event-fx
  :keymap/presets.export
  (fn [{db :db} [_ preset-name]]
    (let [preset (get-in db [:keymap/presets preset-name])
          device (get-in db [:device/current :meta :name])]
      {:settings/export
       {:path [:devices device :keymap :presets preset-name]
        :value preset}})))

(re-frame/reg-event-fx
 :keymap/presets.add
 (fn [{db :db} [_ preset-name layout]]
   ;; TODO: do we really have to save settings manually?
   (let [db (update db :keymap/presets assoc preset-name layout)
         settings (settings/save! db :keymap)]
     {:db (assoc db :settings settings)
      :settings/save settings})))

(re-frame/reg-event-db
 :keymap/presets.name
 (fn [db [_ new-name]]
   (assoc db :keymap/current-preset-name new-name)))

(re-frame/reg-sub
 :keymap/presets.name
 (fn [db _]
   (:keymap/current-preset-name db)))

(defn name! [new-name]
  (re-frame/dispatch [:keymap/presets.name new-name]))

(defn <save-layout>
  []
  (let [save (fn []
               (re-frame/dispatch [:keymap/presets.add
                                   @(re-frame/subscribe [:keymap/presets.name])
                                   (get @(re-frame/subscribe [:keymap/layout])
                                        (dec (events/layer)))]))]
    [:div.modal.fade {:id "chrysalis-plugin-page-keymap-save-layout"}
     [:div.modal-dialog.modal-lg
      [:div.modal-content.bg-faded
       [:div.modal-header
        [:h5.modal-title "Save layout"]
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
               (.modal (js/$ "#chrysalis-plugin-page-keymap-save-layout") "hide")
               (save))}
            [:div.input-group-addon {:title "Name"} [:i.fa.fa-hdd-o]]
            [:input.form-control
             {:type :text
              :value @(re-frame/subscribe [:keymap/presets.name])
              :placeholder "Name your layout..."
              :on-change (fn [e]
                           (re-frame/dispatch [:keymap/presets.name
                                               (.. e -target -value)]))}]]]]]]
       [:div.modal-footer
        [:a.btn.btn-primary
         {:href "#"
          :data-dismiss :modal
          :on-click (fn [e] (save))}
         "Save"]
        [:a.btn.btn-secondary {:href "#"
                               :data-dismiss :modal}
         "Cancel"]]]]]))

(defn- <preset>
  [[preset-name layout]]
  [:div.card {:href "#"
              :key (str "chrysalis-plugin-keymap-preset-" preset-name)}
   [:h5.card-header preset-name]
   [:div.card-block
    [:a.card-text {:href "#"
                   :on-click (fn [e]
                               (re-frame/dispatch [:keymap/load-preset layout]))}
     [layout/<keymap-layout>
      {:device (device/current)
       :svg @(get-in (device/current) [:meta :layout])
       :layout [layout]
       :layer 0
       :props {:style {:width 102 :height 64}}}]]]
   [:div.card-footer.text-left
    [:span.card-text
     [:a {:href "#"
          :title "Export"
          :on-click (fn [e]
                      (.preventDefault e)
                      (re-frame/dispatch [:keymap/presets.export
                                          preset-name]))}
      [:i.fa.fa-share]]]
    [:span.card-text
     [:a {:style {:float :right}
          :href "#"
          :title "Remove"
          :on-click (fn [e]
                      (.preventDefault e)
                      (re-frame/dispatch [:keymap/presets.drop preset-name]))}
      [:i.fa.fa-minus]]]]])

(defn <presets> []
  [:div.card-group
   (doall (map <preset> @(re-frame/subscribe [:keymap/presets])))])
;; TODO: selecting preset be like applying edits
