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

            ;;; ---- Chrysalis ---- ;;;

            ;; Core
            [chrysalis.key-bindings]
            [chrysalis.hardware :as hardware]
            [chrysalis.ui :as ui]
            [chrysalis.ui.page :as page]
            [chrysalis.device :as device]
            [chrysalis.settings :as settings]

            ;; Hardware plugins

            [chrysalis.plugin.hardware.virtual.core]
            [chrysalis.plugin.hardware.model01.core]
            [chrysalis.plugin.hardware.shortcut.core]
            [chrysalis.plugin.hardware.raise.core]


            ;; Page plugins
            [chrysalis.plugin.page.devices.core]
            [chrysalis.plugin.page.led.core]
            [chrysalis.plugin.page.firmware.core]
            [chrysalis.plugin.page.repl.core]
            [chrysalis.plugin.page.spy.core]
            [chrysalis.plugin.page.keymap.core]
            [chrysalis.plugin.page.eeprom.core]

            ;; Kaleidoscope plugins
            [chrysalis.plugin.Kaleidoscope.Colormap.core]
            [chrysalis.plugin.Kaleidoscope.Consumerctl.core]
            [chrysalis.plugin.Kaleidoscope.FingerPainter.core]
            [chrysalis.plugin.Kaleidoscope.HostOS.core]
            [chrysalis.plugin.Kaleidoscope.LEDControl.core]
            [chrysalis.plugin.Kaleidoscope.LED-Palette-Theme.core]
            [chrysalis.plugin.Kaleidoscope.OneShot.core]
            [chrysalis.plugin.Kaleidoscope.Macros.core]
            [chrysalis.plugin.Kaleidoscope.MouseKeys.core]
            [chrysalis.plugin.Kaleidoscope.TapDance.core]))

(re-frame/reg-event-fx
 :db
 [(re-frame/inject-cofx :settings/load)]
 (fn [{:keys [settings db]} _]
   {:db (assoc db
               :page/current :devices
               :settings settings)}))

(re-frame/reg-event-fx
 :settings/window
 (fn [cofx _]
   {:settings/window (get-in cofx [:db :settings])}))

(re-frame/reg-fx
 :settings/window
 (fn [settings]
   (let [browser-window (.. (js/require "electron") -remote getCurrentWebContents
                            getOwnerBrowserWindow)]
     (when (and (get-in settings [:window :x])
                (get-in settings [:window :y]))
       (.setPosition browser-window
                     (get-in settings [:window :x])
                     (get-in settings [:window :y])))
     (.setSize browser-window
               (or (get-in settings [:window :width]) 1200)
               (or (get-in settings [:window :height]) 600))
     (if (get-in settings [:window :isMaximized])
       (.maximize browser-window)
       (.unmaximize browser-window)))))

(re-frame/reg-fx
 :settings/window.update
 (fn [settings]
   (let [browser-window (.. (js/require "electron") -remote getCurrentWebContents
                            getOwnerBrowserWindow)
         bounds (js->clj (.getBounds browser-window))]
     (re-frame/dispatch [:settings/window.save
                         (update settings :window merge
                                 {:width (bounds "width")
                                  :height (bounds "height")
                                  :x (bounds "x")
                                  :y (bounds "y")
                                  :isMaximized (.isMaximized browser-window)})]))))

(re-frame/reg-event-fx
 :settings/window.save
 (fn [cofx [_ new-settings]]
   {:db (assoc (:db cofx) :settings new-settings)
    :settings/save new-settings}))

(re-frame/reg-event-fx
 :settings/window.update
 (fn [cofx _]
   {:settings/window.update (get-in cofx [:db :settings])}))

(re-frame/reg-event-db
 :discard
 (fn [db _]
   db))

(defn ^:export start []
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch-sync [:db])
  (device/detect!)
  (re-frame/dispatch-sync [:settings/window])
  (page/switch-to! :devices)
  (let [usb (js/require "usb")]
    (.on usb "attach" (fn [device] (device/detect!)))
    (.on usb "detach" (fn [device] (device/detect!))))
  (.setTimeout js/window
               (fn []
                 (let [browser-window (.. (js/require "electron") -remote
                                          getCurrentWebContents
                                          getOwnerBrowserWindow)]
                   (doseq [event [:maximize :unmaximize :resize :move]]
                     (.on browser-window (name event)
                          #(re-frame/dispatch [:settings/window.update])))))
               1000)

  (ui/mount-root))

(defn ^:export reload! []
  (re-frame/clear-subscription-cache!)
  (ui/mount-root))
;; TODO: fix bug where nothing shows up on initial load if no settings file
