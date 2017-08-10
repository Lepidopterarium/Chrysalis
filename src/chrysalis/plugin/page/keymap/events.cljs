;; Chrysalis -- Kaleidoscope Command Center
;; Copyright (C) 2017  Simon-Claudius Wystrach <mail@simonclaudius.com>
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

(ns chrysalis.plugin.page.keymap.events
  (:require [chrysalis.command :as command]
            [chrysalis.command.post-process :as post-process]

            [re-frame.core :as re-frame]
            [clojure.walk :as walk]
            [clojure.string :as s]))

;;; ---- Current target ------ ;;;

(re-frame/reg-sub
 :keymap/current-target
 (fn [db _]
   (:keymap/current-target db)))

(re-frame/reg-event-db
 :keymap/current-target
 (fn [db [_ new-target]]
   (assoc db :keymap/current-target new-target)))

(defn current-target []
  @(re-frame/subscribe [:keymap/current-target]))

(defn current-target! [new-target]
  (re-frame/dispatch [:keymap/current-target new-target]))

;;; ---- Layout ---- ;;;

(re-frame/reg-sub
 :keymap/layout
 (fn [db _]
   (:keymap/layout db)))

(re-frame/reg-event-db
 :keymap/layout.process
 (fn [db [_ [_ _ _ response]]]
   (assoc db :keymap/layout (post-process/format :keymap.layout response))))

(re-frame/reg-event-fx
 :keymap/layout!
 (fn [cofx [_ layout]]
   (let [live? (get-in (:db cofx) [:keymap/live-update])]
     (-> {:db (assoc (:db cofx) :keymap/layout layout)}
         (cond->
             live? (assoc :keymap/layout.upload layout))))))

(re-frame/reg-fx
 :keymap/layout
 (fn []
   (command/run :keymap.map nil :keymap/layout.process)))

(re-frame/reg-event-fx
 :keymap/layout.update
 (fn [cofx _]
   {:keymap/layout :update}))

(re-frame/reg-fx
 :keymap/layout.upload
 (fn [layout]
   (command/run :keymap.layout (->> layout
                                flatten
                                (s/join " ")) :discard)))

(re-frame/reg-event-fx
 :keymap/layout.upload
 (fn [cofx _]
   {:keymap/layout.upload (get-in cofx [:db :keymap/layout])}))

(defn layout []
  @(re-frame/subscribe [:keymap/layout]))

(defn layout:update! []
  (re-frame/dispatch [:keymap/layout.update]))

(defn layout:upload! []
  (re-frame/dispatch [:keymap/layout.upload]))
