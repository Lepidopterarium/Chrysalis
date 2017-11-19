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

(ns chrysalis.plugin.Kaleidoscope.OneShot.keymap
  (:require [chrysalis.plugin.page.keymap.events :as events]

            [chrysalis.key :as key]))



;; TODO: wrap in something so it can be activated/deactived on command
(events/add-edit-tab!
  {:title "OneShot Modifiers"
   :modifiers? false
   :keys
   (into []
         (map (fn [{mod :key}]
                {:plugin :oneshot
                 :type :modifier
                 :modifier mod}))
         (events/keys-like #"(left|right)-(control|shift|alt|gui)"))})

(events/add-edit-tab!
  {:title "OneShot Layers"
   :modifiers? false
   :keys (into []
               (map (fn [layer]
                      {:plugin :oneshot
                       :type :layer
                       :layer layer}))
               ;; Docs say 24 layers are supported, by OSM_FIRST - OSM_LAST = 8?
               (range 8))})
