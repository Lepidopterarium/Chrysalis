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

(ns chrysalis.plugin.page.spy.core
  (:require [chrysalis.core :refer [state pages]]
            [chrysalis.ui :refer [page]]))

(defn <spy-item> [index [command response]]
  [:div.row.chrysalis-page-repl-box {:key (str "spy-history-" index)}
   [:div.col-sm-12
    [:div.card.bg-faded
     [:div.card-block
      [:div.card-title.row
       [:div.col-sm-6.text-left
        [:a.chrysalis-link-button.chrysalis-page-repl-collapse-toggle
         {:href (str "#spy-history-collapse-" index)
          :data-toggle :collapse}
         [:i.fa.fa-angle-down]]
        " " [:code command]]]
      [:div.collapse.show {:id (str "spy-history-collapse-" index)}
       [:pre response]]]]]])

(defmethod page [:render :spy] [_ _]
  [:div.container-fluid
   (doall (map (fn [item index]
                 (<spy-item> index item))
               (reverse (partition 2 2 (get-in @state [:command :spy])))
               (range (count (get-in @state [:command :spy])) 0 -1)))])

(swap! pages assoc :spy {:name "Wire Traffic Spy"
                         :index 100})
