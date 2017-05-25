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
  (:require [garden.core :as g]
            [garden.units :as gu]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]

            [chrysalis.ui.page :as page]
            [chrysalis.ui.about :refer [<about>]]
            [chrysalis.ui.main-menu :refer [<main-menu>]]))

;;; ---- Helpers ---- ;;;

(defn style
  ([styles]
   [:style {:type "text/css"}
    (g/css styles)])

  ([]
   (style [:#chrysalis {:margin-top (gu/rem 5)}
           [:#main-menu {:min-width (gu/rem 20)}]
           [:.navbar-brand:hover
            [:i {:animation "fa-spin 2s infinite linear"}]]
           [:.link-button {:color "#292b2c"}]])))

(defn- toHex [i]
  (let [hex (.toString i 16)]
    (if (< i 16)
      (str "0" hex)
      hex)))

(defn color->hex [color]
  (str "#" (apply str (map toHex color))))

;;; ---- Main application ---- ;;;
(defn chrysalis []
  [:div
   [style]
   [<about>]
   [<main-menu>]

   [:div {:id :page}
    (when-let [page (page/current)]
      [page :render])]])

(defn mount-root []
  (reagent/render
   [chrysalis]
   (js/document.getElementById "chrysalis")))
