;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  James Cash <james.cash@occasionallycogent.com>
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

(ns chrysalis.plugin.Kaleidoscope.MouseKeys.keymap
  (:require [chrysalis.plugin.page.keymap.events :as events]))

(events/add-edit-tab!
  {:title "Mouse Keys"
   :modifiers? false
   :keys (->>
           (concat
             (for [dir [:up :down :left :right]]
               {:directions #{dir}})
             (for [ns [:up :down]
                   ew [:left :right]]
               {:directions #{ns ew}})
             (for [btn [:left :middle :right]]
               {:warp-end? true
                :button btn})
             (for [ns [:up :down]
                   ew [:left :right]]
               {:warp? true
                :directions #{ns ew}})
             [{:warp-end? true}]
             (for [dir [:up :down :left :right]]
               {:wheel? true
                :directions #{dir}}))
           (mapv #(assoc % :plugin :mouse-keys)))})
