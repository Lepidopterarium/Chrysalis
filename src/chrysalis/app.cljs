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

(ns chrysalis.app
  (:require [reagent.core :as reagent]

            [chrysalis.core :as core :refer [state pages]]
            [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.settings :as settings]

            ;; Plugins

            [chrysalis.plugin.page.devices.core]
            [chrysalis.plugin.page.repl.core]
            [chrysalis.plugin.page.firmware.core]
            [chrysalis.plugin.page.led.core]
            [chrysalis.plugin.page.spy.core]

            [chrysalis.plugin.Kaleidoscope.FingerPainter.core]
            [chrysalis.plugin.Kaleidoscope.HostOS.core]
            [chrysalis.plugin.Kaleidoscope.LEDControl.core]
            [chrysalis.plugin.Kaleidoscope.OneShot.core]

            [chrysalis.plugin.hardware.virtual.core]
            [chrysalis.plugin.hardware.model01.core]
            [chrysalis.plugin.hardware.shortcut.core]

            [chrysalis.plugin.example.cake.core]))

(enable-console-print!)

(defn init! []
  (device/detect!)

  (let [electron-app (.-app (.-remote (js/require "electron")))
        browser-window (-> (js/require "electron") .-remote .getCurrentWebContents .getOwnerBrowserWindow)]
    (swap! settings/hooks assoc :window {:save (fn []
                                                 (let [bounds (js->clj (.getBounds browser-window))]
                                                   (swap! settings/data assoc-in [:window]
                                                          (merge (get-in @settings/data [:window])
                                                                 {:width (bounds "width")
                                                                  :height (bounds "height")
                                                                  :x (bounds "x")
                                                                  :y (bounds "y")
                                                                  :isMaximized (.isMaximized browser-window)}))))
                                         :load (fn []
                                                 (when (and (get-in @settings/data [:window :x])
                                                            (get-in @settings/data [:window :y]))
                                                   (.setPosition browser-window
                                                                 (get-in @settings/data [:window :x])
                                                                 (get-in @settings/data [:window :y])))
                                                 (.setSize browser-window
                                                           (or (get-in @settings/data [:window :width]) 1200)
                                                           (or (get-in @settings/data [:window :height]) 600))
                                                 (if (get-in @settings/data [:window :isMaximized])
                                                   (.maximize browser-window)
                                                   (.unmaximize browser-window)))})

    (doall (map (fn [event]
                  (.on browser-window (name event) #(settings/save! :window)))
                [:maximize :unmaximize :resize :move])))

  (settings/load!)

  (.setTimeout js/window (fn []
                           (reagent/render
                            [ui/chrysalis]
                            (js/document.getElementById "chrysalis"))

                           (swap! state assoc :page-keys ui/mousetrap)
                           (ui/switch-to-page! (ui/current-page))
                           )
               2000)

  (let [usb (js/require "usb")]
    (.on usb "attach" (fn [device]
                        (device/detect!)))
    (.on usb "detach" (fn [device]
                        (device/detect!)))))

(init!)
