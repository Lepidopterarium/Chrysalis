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
  (:require [reagent.core :as reagent :refer [atom]]

            [clojure.string :as s]))

(defonce state (atom {:devices []
                      :current-device nil
                      :page :devices}))

(defonce pages (atom {}))

(defmulti page
  (fn [p]
    p))

(defmethod page :default [_])

(defn- <menu-item> [[key meta]]
  (let [disabled? (and (:disable? meta)
                       ((:disable? meta)))]
    [:li {:key (str "main-menu-" (name key))
          :class (s/join " " ["nav-item"
                              (when (= key (:page @state))
                                "active")])}
     [:a.nav-link {:href "#"
                   :class (when disabled?
                            "disabled")
                   :on-click (fn [e]
                               (.preventDefault e)
                               (when-not disabled?
                                 (swap! state assoc :page key)))}
      (:name meta)]]))

(defn <settings> []
  (fn []
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
           [:i "There will be settings here, at some point..."]]]]
        ]
       [:div.modal-footer
        [:div {:style {:position :absolute
                       :left "15px"}}
         [:a.btn.btn-danger {:href "#"
                             :on-click (fn [_]
                                         (.close js/window))}
         "Quit"]]
        [:a.btn.btn-primary.disabled {:href "#"
                                      :disabled true}
         "Save"]
        [:a.btn.btn-secondary {:href "#"
                               :data-dismiss :modal}
         "Cancel"]]]]]))

(defn <main-menu> []
  [:nav.navbar.navbar-toggleable-md.navbar-inverse.bg-inverse.fixed-top
   [:button.navbar-toggler.navbar-toggler-right {:type :button
                                                 :data-toggle :collapse
                                                 :data-target "#navbarSupportedContent"
                                                 :aria-controls "navbarSupportedContent"
                                                 :aria-expanded false
                                                 :aria-label "Toggle navigation"}
    [:span.navbar-toggler-icon]]
   [:a.navbar-brand.chrysalis-link-button {:data-toggle :modal
                                           :href "#settings"}
    [:i.fa.fa-spinner] " Chrysalis"]
   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}
    [:ul.navbar-nav.mr-auto
     (doall (map <menu-item>
                 (sort-by (fn [[key meta]] (:index meta)) @pages)))]]
   [:span.navbar-text {:style {:white-space :pre}}
    (when (:current-device @state)
      (get-in @state [:current-device :device :meta :name]))]])
