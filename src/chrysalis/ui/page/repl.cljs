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

(ns chrysalis.ui.page.repl
  (:require [chrysalis.ui :as ui]
            [chrysalis.ui.page :refer [pages page]]
            [chrysalis.utils :refer [state send-command!]]))

(defmethod page :repl [_]
  [:div.container-fluid
   [:div.row.justify-content-center
    [:div.col-12.text-center
     [:h2 "REPL"]]]
   [:div.row.justify-content-left
    [:form.col-sm-12 {:on-submit (fn [e]
                                   (.preventDefault e)
                                   (send-command! (get-in @state [:repl :command]))
                                   (swap! state assoc-in [:repl :command] nil))}
     [:label {:style {:margin-right "1em"}} "❯"]
     [:input {:type :text
              :placeholder "Type command here"
              :style {:border 0
                      :width "75%"}
              :value (get-in @state [:repl :command])
              :on-change (fn [e]
                           (swap! state assoc-in [:repl :command] (.-value (.-target e))))}]]]
   [:div.row
    [:div.col-sm-12
     (doall (map (fn [item index]
                   (ui/display (:command item) (:request item) @(:result item)
                               (str "repl-history-" (- (count (get-in @state [:repl :history])) index))))
                 (get-in @state [:repl :history]) (range)))]]])

(swap! pages assoc :repl {:name "REPL"})
(swap! state assoc :repl {})
