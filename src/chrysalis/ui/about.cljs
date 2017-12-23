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

(ns chrysalis.ui.about)

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
           [:a {:href "https://github.com/Lepidopterarium/Chrysalis"
                :style {:margin :auto}}
            [:img.card-img-top {:alt "Kaleidoscope Logo"
                                :width "350"
                                :height "220"
                                :src "images/kaleidoscope-logo-ph-small.png"}]]
           [:div.card-block
            [:h4.card-title "Chrysalis " (.getVersion app)
             [:small " " [:a.link-button {:href (str "https://github.com/Lepidopterarium/Chrysalis/releases/tag/chrysalis-"
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
