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

(ns chrysalis.plugin.Kaleidoscope.TapDance.core
  (:require [chrysalis.key :as key]))

;; From Kaleidoscope-Ranges.h
(def td-first (+ 0xc009 ; OSL_FIRST
                 7 ; OSL_END
                 1 ; DU_FIRST
                 (bit-shift-left 8 8) ; DUM_LAST
                 1 ; DUL_first
                 (bit-shift-left 8 8) ; DUL_last
                 1))
(def td-last (+ td-first 15))

(defn- tapdance-processor
  [key code]
  (if (<= td-first code td-last)
    (assoc key
           :plugin :tapdance
           :index (- code td-first))
    key))

(swap! key/processors conj tapdance-processor)

(defmethod key/format [:tapdance]
  [key]
  {:extra-text "TapDance"
   :primary-text (str "TD(" (:index key) ")")})

(defmethod key/unformat :tapdance
  [key]
  (+ td-first (:index key)))
