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

(ns chrysalis.hardware.fake
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! close!]]
            [chrysalis.hardware :refer [scan* open known?]]))

(defmethod known? [0xdead 0xbeef] [device]
  (assoc device :meta {:name "An example fake device"}))

(defmethod scan* :fake [_ out]
  (go
    (>! out (clj->js {:comName "<fake>"
                      :manufacturer "Example Corp."
                      :pnpId "<none>"
                      :vendorId "0xdead"
                      :productId "0xbeef"
                      :serialNumber "0xffff"}))))

(defmethod open "<fake>" [_]
  (js-obj
   "command" ""
   "close" (fn [])
   "write" (fn [text callback]
             (this-as self
               (aset self "command" text))
             (callback))
   "drain" (fn [callback]
             (callback))
   "read" (fn [callback]
            "Kaleidoscope/<fake> Fake/Example | Apr 24 2017 15:25:00")))
