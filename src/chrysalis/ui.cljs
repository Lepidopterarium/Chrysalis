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
            [chrysalis.settings :as settings]
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

;;; ---- About ---- ;;;
(defn <about> []
  (let [app (.-app (.-remote (js/require "electron")))
        proc (js/require "process")]
    (fn []
      [:div.modal.fade {:id "about"}
       [:div.modal-dialog
        [:div.modal-content.bg-faded
         [:div.modal-header
          [:h5.modal-title "About"]
          [:button.close {:type :close
                          :data-dismiss :modal}
           [:span "×"]]]
         [:div.modal-body
          [:div.card
           [:a {:href "https://github.com/algernon/Chrysalis"
                :style {:margin :auto}}
            [:img.card-img-top {:alt "Kaleidoscope Logo"
                                :width "350"
                                :height "220"
                                :src "images/kaleidoscope-logo-ph-small.png"}]]
           [:div.card-block
            [:h4.card-title "Chrysalis " (.getVersion app)
             [:small " " [:a.chrysalis-link-button {:href (str "https://github.com/algernon/Chrysalis/releases/tag/chrysalis-"
                                                               (.getVersion app))
                                                    :title "Release notes"}
                          [:i.fa.fa-id-card-o]]]]
            [:div.card-text
             [:div.text-muted
              "Electron/" (.-electron (.-versions proc)) ", Chrome/" (.-chrome (.-versions proc))]
             [:div.text-muted
              "Built by " [:a {:href "https://asylum.madhouse-project.org/"} "Gergely Nagy"]
              " & " [:a {:href "https://github.com/algernon/Chrysalis/graphs/contributors"} "others"] "."]]]]]
         [:div.modal-footer
          [:a.btn.btn-secondary.mr-auto {:href "https://www.gnu.org/licenses/gpl.html"} "License"]
          [:a.btn.btn-info {:href "#" :data-dismiss :modal}
           "Close"]]]]])))

;;; ---- Menu ---- ;;;

(defn- <menu-item> [state [key meta]]
  (let [disabled? (and (:disable? meta)
                       ((:disable? meta)))
        current? (= key (current-page))]
    [:a.dropdown-item {:href "#"
                       :key (str "main-menu-" (name key))
                       :class (when (or disabled? current?) "disabled")
                       :on-click (fn [e]
                                   (.preventDefault e)
                                   (when-not disabled?
                                     (switch-to-page! key)))}
     (if current?
       [:b (:name meta)]
       (:name meta))]))

(defn <main-menu> [state pages]
  [:nav.navbar.navbar-toggleable-md.navbar-inverse.bg-inverse.fixed-top
   [:button.navbar-toggler.navbar-toggler-right {:type :button
                                                 :data-toggle :collapse
                                                 :data-target "#navbarSupportedContent"}
    [:span.navbar-toggler-icon]]
   [:div.dropdown
    [:a.navbar-brand.chrysalis-link-button.text-white {:data-toggle :dropdown
                                                       :href "#"}
     [:i.fa.fa-spinner] " Chrysalis: " [:i (-> (current-page)
                                               pages
                                               :name)]]
    [:div.dropdown-menu
     (doall (map (partial <menu-item> state)
                 (sort-by (fn [[key meta]] (:index meta)) pages)))
     [:hr]
     [:a.dropdown-item {:href "#about"
                        :data-toggle :modal} "About"]
     [:a.dropdown-item {:href "#"
                        :on-click #(.close js/window)} "Quit"]]]
   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}]
   [:span.navbar-text {:style {:white-space :pre}}
    (when-let [device (device/current)]
      (get-in device [:device :meta :name]))]])

;;; ---- Helpers ---- ;;;

(defn- toHex [i]
  (let [hex (.toString i 16)]
    (if (< i 16)
      (str "0" hex)
      hex)))

(defn color->hex [color]
  (str "#" (apply str (map toHex color))))
