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

(ns chrysalis.plugin.page.firmware.events
  (:require [chrysalis.device :as device]
            [chrysalis.command :as command]
            [chrysalis.command.post-process :as post-process]

            [re-frame.core :as re-frame]))

(defonce Avrgirl (js/require "avrgirl-arduino"))

;;; ---- Upload ---- ;;;

(re-frame/reg-fx
 :firmware/upload
 (fn [[device hex-name]]
   (let [avrgirl (Avrgirl. (clj->js {"board" (:board device)
                                     "debug" true}))]
     (re-frame/dispatch [:firmware/state :uploading])
     (device/select! nil)
     (.flash avrgirl hex-name (fn [error]
                                (when error
                                  (.error js/console error))
                                (.setTimeout js/window
                                             (fn []
                                               (device/detect!)
                                               (.setTimeout js/window
                                                            (fn []
                                                              (device/select-by-serial! (:serialNumber device))
                                                              (re-frame/dispatch [:device/open.current])
                                                              (re-frame/dispatch [:firmware/version.update])
                                                              (if error
                                                                (re-frame/dispatch [:firmware/state :error])
                                                                (re-frame/dispatch [:firmware/state :success])))
                                                            1000))
                                             2000))))))

(re-frame/reg-event-fx
 :firmware/upload
 (fn [cofx _]
   (let [device (device/current)
         hex-file (get-in cofx [:db :firmware/hex-file])]
     {:firmware/upload [device hex-file]})))

(defn upload! []
  (re-frame/dispatch [:firmware/upload]))

;;; ---- Version ---- ;;;

(re-frame/reg-sub
 :firmware/version
 (fn [db _]
   (:firmware/version db)))

(re-frame/reg-event-db
 :firmware/version!
 (fn [db [_ [_ _ _ response]]]
   (assoc db :firmware/version (post-process/format :version response))))

(re-frame/reg-fx
 :firmware/version
 (fn []
   (command/run :version [] :firmware/version!)))

(re-frame/reg-event-fx
 :firmware/version.update
 (fn [cofx _]
   {:firmware/version :update}))

(defn version []
  @(re-frame/subscribe [:firmware/version]))

(defn version:update! []
  (re-frame/dispatch [:firmware/version.update]))

;;; ---- Hex file ---- ;;;

(re-frame/reg-sub
 :firmware/hex-file
 (fn [db _]
   (:firmware/hex-file db)))

(re-frame/reg-event-db
 :firmware/hex-file
 (fn [db [_ new-file]]
   (assoc db :firmware/hex-file new-file)))

(defn hex-file []
  @(re-frame/subscribe [:firmware/hex-file]))

(defn hex-file! [new-file]
  (re-frame/dispatch [:firmware/hex-file new-file]))

;;; ---- State ---- ;;;

(re-frame/reg-sub
 :firmware/state
 (fn [db _]
   (:firmware/state db)))

(re-frame/reg-event-db
 :firmware/state
 (fn [db [_ new-state]]
   (assoc db :firmware/state new-state)))

(defn state []
  @(re-frame/subscribe [:firmware/state]))

(defn state! [new-state]
  (re-frame/dispatch [:firmware/state new-state]))
