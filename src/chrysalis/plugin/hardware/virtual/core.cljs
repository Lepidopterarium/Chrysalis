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

(ns chrysalis.plugin.hardware.virtual.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! close!]]
            [clojure.string :as s]

            [chrysalis.command :as command]
            [chrysalis.hardware :refer [scan* open known?]]))

(defmethod known? [0xdead 0xbeef] [device]
  (assoc device
         :meta {:name "An example virtual device"
                :logo "images/plugins/virtual-keyboard.png"
                :layout "images/plugins/virtual-keyboard.svg"
                :matrix [4 4]}))

(defmethod scan* :virtual [_ out]
  (go
    (>! out (clj->js {:comName "<virtual>"
                      :manufacturer "Chrysalis"
                      :pnpId "<none>"
                      :vendorId "0xdead"
                      :productId "0xbeef"
                      :serialNumber "0xffff"}))))

(defmulti command
  (fn [command req]
    (keyword command)))

(defmethod command :default [_ _])

(defmethod command :version [_ _]
  "Kaleidoscope/<virtual> Chrysalis/Virtual Keyboard | Apr 24 2017 15:25:00")

(defmethod command :fingerpainter.palette [_ _]
  (s/join " " (map str [0x00 0x00 0x00
                        230 119 107
                        229 154 67
                        242 228 110
                        155 243 150
                        150 221 243
                        255 255 255])))

(defmethod command :help [_ _]
  (s/join "\n"
          ["help"
           "version"
           "fingerpainter.palette"
           "keymap.map"
           "led.theme"]))

(defmethod command :keymap.map [_ _]
  (s/join " " [89 90 91 0
               92 93 94 98
               95 96 97 0
               0  0  0  0]))

(defmethod command :led.theme [_ _]
  (s/join " " [230 119 107   150 221 243   155 243 150   242 228 110
               229 154  67   230 119 107   150 221 243   155 243 150
               242 228 110   229 154  67   230 119 107   150 221 243
               155 243 150   242 228 110   229 154  67   230 119 107]))

(defn- format-result [text]
  (str text "\r\n.\r\n"))

(defmethod open "<virtual>" [_]
  (js-obj
   "req" ""
   "close" (fn [])
   "write" (fn [text callback]
             (this-as self
               (aset self "req" (.substring text 0 (dec (.-length text)))))
             (callback))
   "drain" (fn [callback]
             (callback)
             (this-as self
               (let [req (aget self "req")
                     cmd (-> req (.split #" ") first)]
                 (command/on-data (format-result (command cmd req))))))))
