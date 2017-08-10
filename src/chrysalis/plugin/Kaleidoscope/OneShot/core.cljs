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

(ns chrysalis.plugin.Kaleidoscope.OneShot.core
  (:require [clojure.string :as s]

            [chrysalis.key :as key :refer [processors]]))

(defn- oneshot-processor [key code]
  (cond
    (and (>= code 0xc001)
         (<= code 0xc008)) (assoc key
                                  :plugin :oneshot
                                  :type :modifier
                                  :modifier (:key (nth key/HID-Codes (+ (- code 0xc001) 224))))
    (and (>= code 0xc009)
         (<= code (+ 0xc009 7))) (assoc key
                                          :plugin :oneshot
                                          :type :layer
                                          :layer (- code 0xc009))
    :default key))

(defmethod key/format [:oneshot] [key]
  {:primary-text (if (= (:type key) :modifier)
                   (:modifier key)
                   (:layer key))
   :extra-text "OneShot"})

(swap! processors concat [oneshot-processor])
