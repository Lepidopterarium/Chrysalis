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

(ns chrysalis.app
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [<!]]

            [chrysalis.hardware :as hardware]
            [chrysalis.command :as command]
            [chrysalis.ui :as ui]

            [chrysalis.ui.FingerPainter]

            [chrysalis.command.LEDControl]

            [chrysalis.hardware.model01]
            [chrysalis.hardware.shortcut]))

(enable-console-print!)

(defonce state (atom {:devices []
                      :repl {}}))

(defn device-open! [device]
  (when (:current-device @state)
    (hardware/close (:current-device @state)))
  (hardware/open device))

(defn device-detect! []
  (swap! state assoc :devices [])
  (let [in (hardware/detect (hardware/scan))]
    (go-loop []
      (when-let [device (<! in)]
        (swap! state (fn [state device]
                       (update-in state [:devices] conj device)) device)
        (recur))))
  nil)

(defn send-command! [req]
  (let [[command & args] (.split req #" +")
        full-args (vec (cons (keyword command) (map (fn [arg]
                                                      (if (= (first arg) ":")
                                                        (.substring arg 1)
                                                        arg))
                                                    args)))
        result (apply command/run (:current-device @state) full-args)]
    (swap! state update-in [:repl :history] conj {:command (keyword command)
                                                  :request req
                                                  :result result})))

(defn <device> [device]
  [:div.col-sm-6 {:key (:comName device)}
   [:div.card
    [:div.card-block
     [:div.card-text
      [:p
       "[Image comes here]"]
      [:p
       (get-in device [:meta :name])]]]
    [:div.card-footer.text-muted
     [:button.btn.btn-primary {:type "button"
                               :on-click #(swap! state assoc :current-device (device-open! (:comName device)))}
      "Select"]]]])

(defn <available-devices> []
  [:div
   [:div.row.justify-content-center
    [:div.col-12.text-center
     [:h2 "Available devices"]]]
   [:div.row
    (map <device> (:devices @state))]])

(defn <repl> []
  [:div
   [:div.row
    [:div.col-sm-12
     [:form {:on-submit (fn [e]
                          (.preventDefault e)
                          (send-command! (get-in @state [:repl :command])))}
      [:input {:type :text
               :placeholder "Type command here"
               :on-change (fn [e]
                            (swap! state assoc-in [:repl :command] (.-value (.-target e))))}]
      ]
     (doall (map (fn [item]
                   (print item)
                   (ui/display (:command item) (:request item) @(:result item)))
                 (get-in @state [:repl :history])))]]])

(defn root-component []
  [:div
   [<available-devices>]
   [<repl>]])

(defn init! []
  (device-detect!))

(reagent/render
 [root-component]
  (js/document.getElementById "application"))

(init!)
