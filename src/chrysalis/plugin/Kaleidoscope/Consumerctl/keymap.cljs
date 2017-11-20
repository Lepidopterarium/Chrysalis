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

(ns chrysalis.plugin.Kaleidoscope.Consumerctl.keymap
  (:require [chrysalis.plugin.page.keymap.events :as events]))

(events/add-edit-tab!
  {:title "Consumer Keys"
   :modifiers? false
   :keys (->>
           [{:key :volume-increment}
            {:key :volume-decrement}
            {:key :volume-mute}
            {:key :play-pause}]
           (mapv #(assoc % :plugin :consumer)))})
