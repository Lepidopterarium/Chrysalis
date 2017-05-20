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

(ns chrysalis.ui.page
  (:require [chrysalis.core :as core]))

;;; ---- Page ---- ;;;

(defmulti page
  (fn [action p]
    [action p]))

(defmethod page :default [_ _ _])

(defn current []
  (:page @core/state))

(defn switch-to! [p]
  (page :leave (current))
  (.reset core/mousetrap)
  (page :enter p)
  (swap! core/state assoc :page p))
