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

(ns chrysalis.hardware
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! close!]]))

(def SerialPort (js/require "serialport"))

(defmulti known?
  (fn [device]
    [(js/parseInt (:vendorId device)) (js/parseInt (:productId device))]))

(defmethod known? :default [d]
  nil)

(defn- jsx->clj
  [x]
  (into {} (for [k (.keys js/Object x)] [(keyword k) (aget x k)])))

(defmulti scan*
  (fn [scanner-type out]
    scanner-type))

(defmethod scan* :serial [_ out]
  (.list SerialPort (fn [err ports]
                      (go
                        (loop [devices ports]
                          (when-let [device (first devices)]
                            (>! out device)
                            (recur (rest devices)))))))
  out)

(defmethod scan* :default [_ out]
  out)

(defn scan []
  (let [out (chan)]
    (scan* :serial out)
    (scan* :virtual out)
    out))

(defn detect [list]
  (let [out (chan)]
    (go-loop []
      (when-let [device (<! list)]
        (when-let [known-device (known? (jsx->clj device))]
          (>! out known-device))
        (recur))
      (close! out))
    out))

(defmulti open
  (fn [device-path]
    device-path))

(defmethod open :default [device-path]
  (let [port (SerialPort. device-path #js {"lock" false})]
    (.once port "open" (fn []
                         (.flush port)
                         (.drain port)
                         (.read port)))
    port))

(defn close [device]
  (.close device))
