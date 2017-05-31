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

(ns chrysalis.plugin.Kaleidoscope.LEDControl.core
  (:require [chrysalis.command.post-process :as post-process]
            [chrysalis.plugin.page.led.theme :refer [<led-theme>]]
            [chrysalis.plugin.page.repl.core :refer [display repl-wrap]]))

(defmethod post-process/format [:led.theme] [_ text]
  (let [theme (map (fn [spec]
                     (map js/parseInt (.split spec #" ")))
                   (remove #{""} (.split text #" *(\d+ \d+ \d+) *")))]
    (vec theme)))

(defmethod display :led.theme [_ req result device index]
  (repl-wrap req index device result
             [<led-theme>
              device
              @(get-in device [:meta :layout])
              result
              {:width 512 :height 320}]))
