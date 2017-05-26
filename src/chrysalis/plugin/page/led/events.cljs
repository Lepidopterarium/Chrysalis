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

(ns chrysalis.plugin.page.led.events
  (:require [chrysalis.command :as command]
            [chrysalis.command.post-process :as post-process]

            [re-frame.core :as re-frame]
            [clojure.walk :as walk]
            [clojure.string :as s]))

;;; ---- Current target ------ ;;;

(re-frame/reg-sub
 :led/current-target
 (fn [db _]
   (:led/current-target db)))

(re-frame/reg-event-db
 :led/current-target
 (fn [db [_ new-target]]
   (assoc db :led/current-target new-target)))

(defn current-target []
  @(re-frame/subscribe [:led/current-target]))

(defn current-target! [new-target]
  (re-frame/dispatch [:led/current-target new-target]))

;;; ---- Theme ---- ;;;

(re-frame/reg-sub
 :led/theme
 (fn [db _]
   (:led/theme db)))

(re-frame/reg-event-db
 :led/theme!
 (fn [db [_ [_ _ _ response]]]
   (assoc db :led/theme (post-process/format :led.theme response))))

(re-frame/reg-fx
 :led/theme
 (fn []
   (command/run :led.theme nil :led/theme!)))

(re-frame/reg-event-fx
 :led/theme.update
 (fn [cofx _]
   {:led/theme :update}))

(re-frame/reg-fx
 :led/theme.upload
 (fn [theme]
   (command/run :led.theme (s/join " " (flatten theme)) :discard)
   ))

(re-frame/reg-event-fx
 :led/theme.upload
 (fn [cofx _]
   {:led/theme.upload (get-in cofx [:db :led/theme])}))

(defn theme []
  @(re-frame/subscribe [:led/theme]))

(defn theme:update! []
  (re-frame/dispatch [:led/theme.update]))

(defn theme:upload! []
  (re-frame/dispatch [:led/theme.upload]))
