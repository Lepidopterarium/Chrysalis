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

(ns chrysalis.ui
  (:require [clojure.string :as s]

            [chrysalis.core :as core]
            [chrysalis.device :as device]))

;;; ---- Page ---- ;;;

(defmulti page
  (fn [action p]
    [action p]))

(defmethod page :default [_ _ _])

(defn current-page []
  (:page @core/state))

(defn switch-to-page! [p]
  (page :leave (current-page))
  (page :enter p)
  (swap! core/state assoc :page p))

;;; ---- Menu ---- ;;;

(defn- <menu-item> [state [key meta]]
  (let [disabled? (and (:disable? meta)
                       ((:disable? meta)))]
    [:li {:key (str "main-menu-" (name key))
          :class (s/join " " ["nav-item"
                              (when (= key (current-page))
                                "active")])}
     [:a.nav-link {:href "#"
                   :class (when disabled?
                            "disabled")
                   :on-click (fn [e]
                               (.preventDefault e)
                               (when-not disabled?
                                 (switch-to-page! key)))}
      (:name meta)]]))

(defn <main-menu> [state pages detector]
  [:nav.navbar.navbar-toggleable-md.navbar-inverse.bg-inverse.fixed-top
   [:button.navbar-toggler.navbar-toggler-right {:type :button
                                                 :data-toggle :collapse
                                                 :data-target "#navbarSupportedContent"}
    [:span.navbar-toggler-icon]]
   [:div.dropdown
    [:a.navbar-brand.chrysalis-link-button.text-white {:data-toggle :dropdown
                                                       :href "#"}
     [:i.fa.fa-spinner] " Chrysalis"]
    [:div.dropdown-menu
     [:a.dropdown-item {:href "#"
                        :on-click detector}
      "Scan devices"]
     [:a.dropdown-item {:href "#settings"
                        :data-toggle :modal}
      "Settings"]
     [:hr]
     [:a.dropdown-item {:href "#"
                        :on-click #(.close js/window)} "Quit"]]]

   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}
    [:ul.navbar-nav.mr-auto
     (doall (map (partial <menu-item> state)
                 (sort-by (fn [[key meta]] (:index meta)) pages)))]]
   [:span.navbar-text {:style {:white-space :pre}}
    (when-let [device (device/current)]
      (get-in device [:device :meta :name]))]])

;;; ---- Settings ---- ;;;

(defn <settings> []
  [:div.modal.fade {:id "settings"}
   [:div.modal-dialog.modal-lg
    [:div.modal-content.bg-faded
     [:div.modal-header
      [:h5.modal-title "Settings"]
      [:button.close {:type :close
                      :data-dismiss :modal}
       [:span "×"]]]
     [:div.modal-body
      [:div.container-fluid
       [:div.row
        [:div.col-sm-12
         [:i "There will be settings here, at some point..."]]]]]
     [:div.modal-footer
      [:a.btn.btn-primary.disabled {:href "#"
                                    :disabled true}
       "Save"]
      [:a.btn.btn-secondary {:href "#"
                             :data-dismiss :modal}
       "Cancel"]]]]])
