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

(ns chrysalis.plugin.page.firmware.core
  (:require [chrysalis.device :as device]
            [chrysalis.ui.page :as page]
            ;;[chrysalis.settings :as settings]

            [chrysalis.plugin.page.firmware.events :as events]
            [chrysalis.plugin.page.firmware.views :as views]))


(comment

(swap! settings/hooks assoc :firmware {:save (fn []
                                               (let [device (keyword (get-in (device/current) [:device :meta :name]))]
                                                 (swap! settings/data assoc-in [:devices device :firmware :latest-hex]
                                                        (get-in @state [:firmware :hex-file]))))
                                       :load (fn [] nil)})
)

(page/add! :firmware {:name "Firmware Flasher"
                      :index 80
                      :disable? (fn [] (nil? (device/current)))
                      :render views/render
                      :device/need? true
                      :events {:firmware/version :update}})
