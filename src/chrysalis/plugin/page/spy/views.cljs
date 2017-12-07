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

(ns chrysalis.plugin.page.spy.views
  (:require [chrysalis.ui :as ui]
            [chrysalis.command :as command]

            [chrysalis.plugin.page.repl.core :as repl]

            [clojure.string :as s]))

(defn- <pending-item> [[device command args]]
  [:div.row.box
   [:div.col-sm-12
    [:div.card.bg-faded
     [:div.card-block
      [:div.card-title.row
       [:div.col-sm-6.text-left
        " " [:code command " " (if (coll? args) (apply str args) (str args))]]]]]]])


(defn- <spy-item> [index [device command args response]]
  [:div.row.box {:key (str "spy-history-" index)}
   [:div.col-sm-12
    [:div.card.bg-faded
     [:div.card-block
      [:div.card-title.row
       [:div.col-sm-6.text-left
        [:a.link-button.collapse-toggle
         {:href (str "#spy-history-collapse-" index)
          :data-toggle :collapse}
         [:i.fa.fa-angle-down]]
        " " [:code command " " (if (coll? args) (apply str args) (str args))]]]
      [:div.collapse.show {:id (str "spy-history-collapse-" index)}
       [:pre (clj->js response) "\r\n."]
       [:div.text-muted.text-right.device-name
        (get-in device [:meta :name])]]]]]])

(defn render []
  [:div.container-fluid {:id :spy}
   [ui/style (repl/style :#spy)]

   [:h3 "Pending Commands"]
   (into
     [:div.pending]
     (for [in-flight (command/pending)]
       [<pending-item> in-flight]))

   [:h3 "Sent Commands"]
   (into
     [:div.sent]
     (for [[index item] (map-indexed vector (command/history))]
       [<spy-item> index item]))])
