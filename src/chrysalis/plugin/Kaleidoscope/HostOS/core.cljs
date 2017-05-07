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

(ns chrysalis.plugin.Kaleidoscope.HostOS.core
  (:require [chrysalis.command :refer [pre-process* process*]]))

(def hostos-types ["Linux"
                   "MacOS X"
                   "Windows"
                   "Other"])

(defmethod process* :hostos.type [_]
  (fn [result text]
    (let [type-index (js/parseInt text)]
      (if (= type-index 255)
        (reset! result "Automatic")
        (reset! result (nth hostos-types type-index))))))

(defmethod pre-process* :hostos.type [_ args]
  (condp = (first args)
    "linux" ["0"]
    "osx" ["1"]
    "windows" ["2"]
    "other" ["3"]
    "auto" ["255"]
    args))
