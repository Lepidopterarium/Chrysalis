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
  (:require [chrysalis.hardware :as hardware]
            [chrysalis.device :as device]
            [chrysalis.key :as key]
            [chrysalis.command.post-process :as post-process]
            [chrysalis.ui :as ui]
            [chrysalis.ui.page :as page]

            [chrysalis.plugin.page.repl.events :as events]

            [clojure.string :as s]
            [garden.units :as gu]))


(defn repl-wrap [req index device result output]
  (let [latest? (= index (count (events/history)))]
    [:div.row.box {:key (str "repl-history-" index)}
     [:div.col-sm-12
      [:div.card {:class (when latest? "card-outline-success")}
       [:div.card-block
        [:div.card-title.row
         [:div.col-sm-6.text-left
          [:a.link-button.collapse-toggle
           {:href (str "#repl-history-collapse-" index)
            :data-toggle :collapse}
           [:i.fa.fa-angle-down]]
          " " [:code req]]
         [:div.col-sm-6.text-right
          [:a.link-button {:href "#"
                           :on-click (fn [e]
                                       (.preventDefault e)
                                       (events/input! req)
                                       (.focus (js/document.getElementById "repl-prompt-input")))}
           [:i.fa.fa-repeat]]]]
        [:div.collapse.show {:id (str "repl-history-collapse-" index)}
         (if result
           (if-not (= output [:pre "\"\""])
             output
             [:pre [:i "<no output>"]])
           [:i.fa.fa-refresh.fa-spin])
         [:div.text-muted.text-right.device-name
          (get-in device [:meta :name])]]]]]]))

(defn- <command> [cmd]
  [:button.btn.btn-outline-primary
   {:type :button
    :key (str "repl-avail-command-modal-" cmd)
    :data-dismiss :modal
    :on-click (fn [e]
                (events/input! (name cmd))
                (.focus (js/document.getElementById "repl-prompt-input")))}
   cmd])

(defmulti display
  (fn [command _ _ _ _]
    command))

(defmethod display :default [_ req result device index]
  (repl-wrap req index device result
             [:pre (.stringify js/JSON (clj->js result) nil 2)]))

(defn- <help-command-group> [index [group members]]
  [:div.card {:key (str "repl-help-" index "-" group)}
   [:div.card-block
    [:div.card-title
     [:h5 group]]
    [:div.card-text
     [:div.btn-group
      (doall (map <command> (sort members)))]]]])

(defn- group-commands [commands]
  (sort
   (group-by (fn [s] (first (.split (name s) "."))) commands)))

(defmethod display :help [_ req result device index]
  (repl-wrap req index device result
             [:div.card-group
              (doall (map (partial <help-command-group> index)
                          (group-commands result)))]))

(defn- <key> [history-index layer key idx]
  (key/display (str "repl-keymap-layer-" history-index "-" layer "-" idx) key))

(defn- <layer> [index keymap layer]
  [:div.col-sm-12 {:key (str "repl-keymap-layer-" index "-" layer)}
   [:div.card
    [:div.card-block
     [:div.card-title
      [:b "Layer #" layer]]
     [:div.card-text
      (doall (map (partial <key> index layer) keymap (range)))]]]])

(defmethod display :keymap.map [_ req result device index]
  (let [layer-size (apply * (get-in device [:meta :matrix]))]
    (repl-wrap req index device result
               [:div.row
                (doall (map (partial <layer> index)
                            (partition layer-size result)
                            (range)))])))

(defn style [page]
  [:#page
   [page
    [:.prompt :.box {:margin-bottom (gu/em 1)}]
    [:.prompt
     ["[type=submit]" {:background :none
                       :padding (gu/px 0)
                       :border (gu/px 0)
                       :cursor :pointer}]
     ["[type=submit]:hover" {:color "#014c8c"}]
     [:.dropdown-menu {:min-width (gu/px 0)
                       :left :auto
                       :right (gu/px 0)}]]

    [:.box
     [:.card-block
      [:.collapse
       [:pre {:white-space :pre-wrap}]
       [:.device-name {:margin-top (gu/em 1)}]]]]

    [:.collapse-toggle
     [:i {:width (gu/px 10.38)
          :display :inline-block}]]
    [:.collapse-toggle.collapsed
     [:i:before {:content "\f105"}]]]])

(defn render []
  [:div.container-fluid {:id :repl}
   [ui/style (style :#repl)]
   [:div.row.justify-content-left.prompt
    [:form.col-sm-12 {:on-submit (fn [e]
                                   (.preventDefault e)
                                   (events/command:send!))}
     [:div.input-group
      [:span.input-group-addon
       [:i.fa.fa-angle-right]]
      [:input.form-control {:id "repl-prompt-input"
                            :type :text
                            :placeholder "Type command here"
                            :autoFocus true
                            :value (events/input)
                            :on-key-down (fn [key]
                                           (case (.-which key)
                                             38 (events/history:previous!)
                                             40 (events/history:next!)
                                             nil))
                            :on-change #(events/input! (-> % .-target .-value))}]
      [:span.input-group-addon
       [:input.nav-link {:type :submit
                         :value ""
                         :title "Run!"}]]
      [:div.dropdown.input-group-addon
       [:a.dropdown-toggle.link-button {:href "#"
                                        :data-toggle :dropdown}
        [:i.fa.fa-cogs]]
       [:div.dropdown-menu.text-right
        [:a.link-button.dropdown-item {:href "#"
                                       :title "List of available commands"
                                       :on-click #(events/command:run! "help")}
         "Help"]
        [:hr]
        [:a.link-button.dropdown-item {:href "#"
                                       :title "Clear the REPL history"
                                       :on-click (fn [e]
                                                   (.preventDefault e)
                                                   (events/history:clear!))}
         "Clear history"]]]]]]
   (let [history (events/history post-process/format)]
     (doall (map (fn [[device command args response] index]
                   (display command (str (name command) " " args) response device index))
                 history
                 (range (count history) 0 -1))))])

(page/add! :repl {:name "REPL"
                  :index 99
                  :disable? (fn [] (nil? (device/current)))
                  :render render
                  :device/need? true})
