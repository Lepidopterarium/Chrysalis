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

(ns chrysalis.plugin.hardware.model01.core
  (:require [chrysalis.hardware :refer [known?]]
            [re-frame.core :as re-frame]))

(defmethod known? [0x1209 0x2301] [device]
  (assoc device
         :meta {:name "Keyboardio Model 01"
                :logo "images/plugins/model01.png"
                :layout (re-frame/subscribe [:device/svg "images/plugins/model01.svg"])
                :matrix [16 4]}
         :led {:map [[3 4 11 12 19 20 26 27      36 37 43 44 51 52 59 60]
                     [2 5 10 13 18 21 25 28      35 38 42 45 50 53 58 61]
                     [1 6  9 14 17 22 24 29      34 39 41 46 49 54 57 62]
                     [0 7  8 15 16 23 31 30      33 32 40 47 48 55 56 63]]}
         :board {:name "Keyboard.io Model 01"
                 :baud 9600
                 :productId ["0x2300" "0x2301"]
                 :protocol "avr109"
                 :signature (js/Buffer. #js [0x43 0x41 0x54 0x45 0x52 0x49 0x4e])}))
