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

(ns chrysalis.plugin.Kaleidoscope.Macros.core
  (:require [chrysalis.key :as key]))

(defn- macros-processor [key code]
  (let [flags (bit-shift-right code 8)
        key-code (bit-and code 0x00ff)]
    (if (= flags (-> 0
                     (bit-set 6) ;SYNTHETIC
                     (bit-set 5)));IS_MACRO
      {:plugin :macros
       :index key-code}
      key)))

(swap! key/processors conj macros-processor)

(defmethod key/format [:macros]
  [key]
  {:primary-text (str "M(" (:index key) ")")})

(defmethod key/unformat :macros
  [key]
  (-> 0 (bit-set 6) (bit-set 5) (bit-shift-left 8)
      (bit-or (:index key))))
