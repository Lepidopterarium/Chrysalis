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
    [:div.row.chrysalis-page-repl-box {:key (str "repl-history-" index)}
     [:div.col-sm-12
      [:div.card {:class (when latest? "card-outline-info")}
       [:div.card-block
        [:div.card-title.row
         [:div.col-sm-6.text-left
          [:a.chrysalis-link-button.chrysalis-page-repl-collapse-toggle
           {:href (str "#repl-history-collapse-" index)
            :data-toggle :collapse}
           [:i.fa.fa-angle-down]]
          " " [:code req]]
         [:div.col-sm-6.text-right
          [:a.chrysalis-link-button {:on-click (fn [e]
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
               [:pre (.stringify js/JSON (clj->js result) nil 2)])))

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

   [:div.row.justify-content-left.chrysalis-page-repl-prompt
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
                         :value ""
                         :title "Run!"}]]
      [:div.dropdown.input-group-addon
       [:a.dropdown-toggle.chrysalis-link-button {:href "#"
                                                  :data-toggle :dropdown}
        [:i.fa.fa-cogs]]
       [:div.dropdown-menu.text-right
        [:a.chrysalis-link-button.dropdown-item {:href "#"
                                                 :title "Clear the REPL history"
                                                 :on-click (fn [e]
                                                             (.preventDefault e)
                                                             (swap! state assoc-in [:repl :history] []))}
         "Clear history"]
        (when (get-in @state [:repl :available-commands])
          [:a.chrysalis-link-button.dropdown-item {:href "#"
                                                   :data-toggle :modal
                                                   :data-target "#repl-available-commands"
                                                   :title "List of available commands"}
           "Help"])]]]]]
   (doall (map (fn [item index]
                 (display (:command item) (:request item) @(:result item) index))
               (get-in @state [:repl :history])
               (range (count (get-in @state [:repl :history])) 0 -1)))])

(swap! pages assoc :repl {:name "REPL"
                          :index 99
                          :disable? (fn [] (nil? (get-in @state [:current-device :port])))})
(swap! state assoc :repl {:history []})
