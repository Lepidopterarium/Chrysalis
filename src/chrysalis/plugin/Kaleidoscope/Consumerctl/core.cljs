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

(ns chrysalis.plugin.Kaleidoscope.Consumerctl.core
  (:require [chrysalis.key :as key]))

;; TODO: implement the rest of the consumer key codes?
;; There are a lot of them and they seem awful specific
(defn- consumer-processor
  [key code]
  (let [flags (bit-shift-right code 8)
        key-code (bit-and code 0x00ff)]
    (if (and (not= key 0xffff)
             (bit-test flags 6) ;synthetic
             (bit-test flags 1));is_consumer
      (cond
        (and (bit-test flags 2) (bit-test flags 4)) ; HID_TYPE_RTC
        (case key-code
          0xE9 (assoc key :plugin :consumer
                      :key :volume-increment)
          0xEA (assoc key :plugin :consumer
                      :key :volume-decrement)
          key)

        (and (bit-test flags 2) (bit-test flags 3) ; HID_TYPE_OOC
             (= key-code 0xE2))
        (assoc key :plugin :consumer
               :key :volume-mute)

        (and (bit-test flags 4) ; HID_TYPE_OSC
             (= key-code 0xCD))
        (assoc key :plugin :consumer
               :key :play-pause))
      key)))

(swap! key/processors conj consumer-processor)

(defmethod key/format [:consumer]
  [key]
  {:extra-text "Consumer"
   :primary-text (case (:key key)
                   :volume-increment "🔊"
                   :volume-decrement "🔉"
                   :volume-mute "🔇"
                   :play-pause "🢒/⏸")})
