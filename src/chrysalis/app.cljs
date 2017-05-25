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
  (:require [re-frame.core :as re-frame]
            [re-frisk.core :refer [enable-re-frisk!]]

            ;;; ---- Chrysalis ---- ;;;

            ;; Core
            [chrysalis.key-bindings]
            [chrysalis.hardware :as hardware]
            [chrysalis.ui :as ui]
            [chrysalis.device :as device]

            ;; Hardware plugins

            [chrysalis.plugin.hardware.virtual.core]
            [chrysalis.plugin.hardware.model01.core]
            [chrysalis.plugin.hardware.shortcut.core]

            ;; Page plugins
            [chrysalis.plugin.page.devices.core]
            [chrysalis.plugin.page.led.core]
            [chrysalis.plugin.page.firmware.core]
            [chrysalis.plugin.page.repl.core]
            [chrysalis.plugin.page.spy.core]

            ;; Kaleidoscope plugins
            [chrysalis.plugin.Kaleidoscope.FingerPainter.core]
            [chrysalis.plugin.Kaleidoscope.HostOS.core]
            [chrysalis.plugin.Kaleidoscope.LEDControl.core]
            [chrysalis.plugin.Kaleidoscope.OneShot.core]))

(enable-console-print!)
(enable-re-frisk! {:width "600px"
                   :height "400px"})

(comment (defn init! []
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

           

           ))

(re-frame/reg-event-db
 :db
 (fn [_ _]
   {:page/current :devices}))

(defn ^:export start []
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch-sync [:db])
  (device/detect!)
  (let [usb (js/require "usb")]
    (.on usb "attach" (fn [device]
                        (device/detect!)))
    (.on usb "detach" (fn [device]
                        (device/detect!))))
  (ui/mount-root))

(defn ^:export reload! []
  (re-frame/clear-subscription-cache!)
  (ui/mount-root))
