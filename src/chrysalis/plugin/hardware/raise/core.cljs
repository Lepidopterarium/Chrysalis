;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  Simon-Claudius Wystrach <mail@simonclaudius.com>
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
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns chrysalis.plugin.hardware.raise.core
  (:require [chrysalis.hardware :refer [known?]]
            [re-frame.core :as re-frame]))


;; The following still needs to be completed.

(defmethod known? [0xfeed 0x6060] [device]
  (assoc device
         :meta {:name "Dygma Raise"
                :logo "images/plugins/dygma-raise.png"
                :layout (re-frame/subscribe [:device/svg "images/plugins/dygma-raise.svg"])
                :matrix []}


        :board {:name "Dygma Raise"
                 :baud 9600
                 :productId ["" ""]
                 :protocol ""
                 :manualReset true
                 :signature (js/Buffer. #js [])}))
