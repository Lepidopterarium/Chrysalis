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

(ns chrysalis.plugin.page.repl.core
  (:require [reagent.core :as reagent]
            [chrysalis.command :as command]
            [chrysalis.ui :refer [pages page state]]))

(defn- send-command! [req]
  (let [[command & args] (.split req #" +")
        full-args (vec (cons (keyword command) (map (fn [arg]
                                                      (if (= (first arg) ":")
                                                        (.substring arg 1)
                                                        arg))
                                                    args)))
        result (apply command/run (get-in @state [:current-device :port]) full-args)]
    (swap! state update-in [:repl :history] (fn [s x]
                                              (cons x s))
           {:command (keyword command)
            :request req
            :result result})))

(defn repl-wrap [req index result]
  (let [latest? (= index (count (get-in @state [:repl :history])))]
    [:div.row {:key (str "repl-history-" index)
               :style {:margin-bottom "1em"}}
     [:div.col-sm-12
      [:div.card {:class (when latest? "card-outline-info")}
       [:div.card-block
        [:div.card-title.row {:style {:margin-bottom "0px"}}
         [:div.col-sm-6.text-left
          [:a {:href (str "#repl-history-collapse-" index)
               :data-toggle :collapse
               :style {:color "#292b2c"}}
           [:i.fa.fa-angle-down]]
          " " [:code req]]
         [:div.col-sm-6.text-right
          [:a {:style {:color "#292b2c"}
               :on-click (fn [e]
                           (.preventDefault e)
                           (swap! state assoc-in [:repl :command] req)
                           (.focus (js/document.getElementById "repl-prompt-input")))}
           [:i.fa.fa-repeat]]]]
        [:div {:id (str "repl-history-collapse-" index)
               :class (str "collapse " (when latest? "show"))}
         (if-not (= result [:pre "\"\""])
           result
           [:pre [:i "<no output>"]])]]]]]))

(defn- <command> [cmd]
  [:button.btn.btn-outline-primary
   {:type :button
    :key (str "repl-avail-command-modal-" cmd)
    :data-dismiss :modal
    :on-click (fn [e]
                (swap! state assoc-in [:repl :command] (name cmd))
                (.focus (js/document.getElementById "repl-prompt-input")))}
   cmd])

(defmulti display
  (fn [command _ _ _]
    command))

(defmethod display :default [_ req result index]
  (when result
    (repl-wrap req index
               [:pre {:style {:white-space :pre-wrap}} (.stringify js/JSON (clj->js result) nil 2)])))

(defmethod display :help [_ req result index]
  (when result
    (repl-wrap req index
               [:div.list-group.col-sm-2
                (doall (map <command> (sort result)))])))

(defn- available-commands []
  (when (get-in @state [:current-device :port])
    (let [r (command/run (get-in @state [:current-device :port]) :help)]
     (swap! state assoc-in [:repl :available-commands] r))))

(defmethod page :repl [_]
  (when-not (get-in @state [:repl :available-commands])
    (available-commands))
  [:div.container-fluid
   [:div.modal.fade {:id "repl-available-commands"}
    [:div.modal-dialog
     [:div.modal-content
      [:div.modal-header
       [:h5.modal-title "Available commands"]]
      [:div.modal-body
       [:div.list-group
        (when (get-in @state [:repl :available-commands])
          (doall (map <command> (sort @(get-in @state [:repl :available-commands])))))]]
      [:div.modal-footer
       [:button.btn.btn-secondary
        {:type :button
         :data-dismiss :modal} "Cancel"]]]]]

   [:div.row.justify-content-left {:style {:margin-bottom "1em"}}
    [:form.col-sm-12 {:on-submit (fn [e]
                                   (.preventDefault e)
                                   (send-command! (get-in @state [:repl :command]))
                                   (swap! state assoc-in [:repl :command] nil))}
     [:div.input-group
      [:span.input-group-addon
       [:i.fa.fa-angle-right]]
      [:input.form-control {:id "repl-prompt-input"
                            :type :text
                            :placeholder "Type command here"
                            :autoFocus true
                            :value (get-in @state [:repl :command])
                            :on-change (fn [e]
                                         (swap! state assoc-in [:repl :command] (.-value (.-target e))))}]
      [:span.input-group-addon
       [:input.nav-link {:type :submit
                         :value "⏎"
                         :title "Run!"}]]
      [:span.input-group-addon
       [:a {:href "#"
            :style {:color "#292b2c"}
            :title "Clear the REPL history"
            :on-click (fn [e]
                        (.preventDefault e)
                        (swap! state assoc-in [:repl :history] []))} [:i.fa.fa-eraser]]]
      (when (get-in @state [:repl :available-commands])
        [:span.input-group-addon
         [:a {:href "#"
              :style {:color "#292b2c"}
              :data-toggle :modal
              :data-target "#repl-available-commands"
              :title "List of available commands"}
          [:i.fa.fa-info]]])]]]
   (doall (map (fn [item index]
                 (display (:command item) (:request item) @(:result item) index))
               (get-in @state [:repl :history])
               (range (count (get-in @state [:repl :history])) 0 -1)))])

(swap! pages assoc :repl {:name "REPL"
                          :index 1
                          :disable? (fn [] (nil? (get-in @state [:current-device :port])))})
(swap! state assoc :repl {:history []})