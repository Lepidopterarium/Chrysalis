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
  (:require [chrysalis.hardware :refer [known?]]))

(defmethod known? [0x1209 0x2301] [device]
  (assoc device
         :meta {:name "Keyboardio Model 01"
                :logo "images/plugins/model01.png"
                :layout "images/plugins/model01.svg"
                :matrix [16 4]}
         :led {:map [[3 4 11 12 19 20 26 27      36 37 43 44 51 52 59 60]
                     [2 5 10 13 18 21 25 28      35 38 42 45 50 53 58 61]
                     [1 6  9 14 17 22 24 29      34 39 41 46 49 54 57 62]
                     [0 7  8 15 16 23 31 30      33 32 40 47 48 55 56 63]]
               :gamma [0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
                       0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,
                       1,  1,  1,  1,  1,  1,  1,  1,  1,  2,  2,  2,  2,  2,  2,  2,
                       2,  3,  3,  3,  3,  3,  3,  3,  4,  4,  4,  4,  4,  5,  5,  5,
                       5,  6,  6,  6,  6,  7,  7,  7,  7,  8,  8,  8,  9,  9,  9, 10,
                       10, 10, 11, 11, 11, 12, 12, 13, 13, 13, 14, 14, 15, 15, 16, 16,
                       17, 17, 18, 18, 19, 19, 20, 20, 21, 21, 22, 22, 23, 24, 24, 25,
                       25, 26, 27, 27, 28, 29, 29, 30, 31, 32, 32, 33, 34, 35, 35, 36,
                       37, 38, 39, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 50,
                       51, 52, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 66, 67, 68,
                       69, 70, 72, 73, 74, 75, 77, 78, 79, 81, 82, 83, 85, 86, 87, 89,
                       90, 92, 93, 95, 96, 98, 99,101,102,104,105,107,109,110,112,114,
                       115,117,119,120,122,124,126,127,129,131,133,135,137,138,140,142,
                       144,146,148,150,152,154,156,158,160,162,164,167,169,171,173,175,
                       177,180,182,184,186,189,191,193,196,198,200,203,205,208,210,213,
                       215,218,220,223,225,228,231,233,236,239,241,244,247,249,252,255]}
         :board {:name "Keyboard.io Model 01"
                 :baud 9600
                 :productId ["0x2300" "0x2301"]
                 :protocol "avr109"
                 :signature (js/Buffer. #js [0x43 0x41 0x54 0x45 0x52 0x49 0x4e])}))
