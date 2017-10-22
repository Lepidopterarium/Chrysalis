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

            [chrysalis.key :as key]))

(def osm-start 0xc001)
(def osm-end (+ osm-start 7))
(def osm-offset 224) ; Code for first modifier (control_l)
(def osl-start 0xc009)
;; Docs
(def osl-end (+ osl-start 7))

(defn- oneshot-processor [key code]
  (cond
    (<= osm-start code osm-end)
    (assoc key
           :plugin :oneshot
           :type :modifier
           :modifier (:key (nth key/HID-Codes (+ (- code osm-start) osm-offset))))

    (<= osl-start code osl-end)
    (assoc key
           :plugin :oneshot
           :type :layer
           :layer (- code osl-start))

    :else key))

(swap! key/processors conj oneshot-processor)

(defmethod key/format [:oneshot]
  [key]
  {:primary-text (if (= (:type key) :modifier)
                   (:primary-text (key/format {:plugin :core :key (:modifier key)}))
                   (str "L" (:layer key)))
   :extra-text "OS"})

(defmethod key/unformat :oneshot
  [{:keys [modifier layer type] :as key}]
  (case type
    :modifier (let [code (->> key/HID-Codes
                             (map-indexed vector)
                             (some (fn [[i {k :key}]] (when (= k modifier) i))))]
                (+ osm-start (- code osm-offset)))
    :layer (+ layer osl-start)))
