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

(ns chrysalis.command.post-process
  (:require [re-frame.core :as re-frame]))

(defmulti format
  (fn [command _]
    [command]))

(defmethod format [:version] [_ text]
  (let [[version-and-device date] (.split text #" \| ")
        t (.split version-and-device #" ")
        fwver (-> t
                  first
                  (.split #"/")
                  second)
        [manufacturer product] (-> t
                                   (.slice 1)
                                   (.join " ")
                                   (.split #"/"))]
    {:device {:manufacturer manufacturer
              :product product}
     :firmware-version fwver
     :timestamp date}))

(defmethod format [:help] [_ text]
  (->> (.split text #"\r?\n")
       (map keyword)
       vec))

(defmethod format [:layer.getState] [_ text]
  (->> (map (fn [state idx] [idx (= state "1")]) text (range))
       (filter second)
       (map first)
       vec))

(defmethod format :default [_ text]
  text)
