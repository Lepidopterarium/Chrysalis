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

(ns chrysalis.ui.main-menu
  (:require [chrysalis.ui.page :as page]
            [chrysalis.device :as device]))

(defn- <menu-item> [[key meta] index]
  (let [disabled? (and (:disable? meta)
                       ((:disable? meta)))
        current? (= key (:key (page/current)))]
    [:a.dropdown-item {:href "#"
                       :key (str "main-menu-" (name key))
                       :class (when (or disabled? current?) "disabled")
                       :on-click (fn [e]
                                   (.preventDefault e)
                                   (when-not disabled?
                                     (page/switch-to! key)))}
     (if current?
       [:b (:name meta)]
       (:name meta))

     [:div.text-right.text-mute {:style {:float :right}} "Alt+" index]]))

(defn <main-menu> []
  [:nav.navbar.navbar-expand-lg.navbar-dark.bg-dark.fixed-top
   [:button.navbar-toggler.navbar-toggler-right {:type :button
                                                 :data-toggle :collapse
                                                 :data-target "#navbarSupportedContent"}
    [:span.navbar-toggler-icon]]
   [:div.dropdown
    [:a.navbar-brand.link-button.text-white {:data-toggle :dropdown
                                             :href "#"}
     [:i.fa.fa-spinner] " Chrysalis: " [:b (-> (page/current)
                                               :name)]]
    [:div.dropdown-menu {:id "main-menu"}
     (let [pages (map-indexed vector (sort-by (fn [[key meta]]
                                                (:index meta))
                                              (page/list)))]
       (doall
        (for [[index menu-item] pages]
          ^{:key index}
          [<menu-item> menu-item index])))
     [:hr]
     [:a.dropdown-item {:href "#about"
                        :data-toggle :modal} "About"]
     [:a.dropdown-item {:href "#"
                        :on-click #(.close js/window)} "Quit"
      [:div.text-right.text-mute {:style {:float :right}} "Ctrl+Q"]]]]
   [:div.collapse.navbar-collapse {:id "navbarSupportedContent"}]
   [:span.navbar-text {:style {:white-space :pre}}
    (when-let [device (device/current)]
      (get-in device [:meta :name]))]])
