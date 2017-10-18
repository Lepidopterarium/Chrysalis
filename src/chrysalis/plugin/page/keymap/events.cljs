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
   (assoc db :keymap/layout (post-process/format :keymap.map response))))

(re-frame/reg-event-fx
 :keymap/layout!
 (fn [{db :db :as cofx} [_ layout]]
   (let [live? (db :keymap/live-update)]
     (-> {:db (assoc db :keymap/layout layout)}
         (cond->
             live? (assoc :keymap/layout.upload layout))))))

(re-frame/reg-fx
 :keymap/layout
 (fn []
   (command/run :keymap.map nil :keymap/layout.process)))

(re-frame/reg-event-fx
  :keymap/change-key!
  (fn [{db :db} [_ layer index new-key]]
    {:db (assoc-in db [:keymap/layout layer index] new-key)}))

(re-frame/reg-event-fx
 :keymap/layout.update
 (fn [cofx _]
   {:keymap/layout :update}))

(re-frame/reg-fx
 :keymap/layout.upload
 (fn [layout]
   (command/run :keymap.layout
     (->> layout flatten (s/join " "))
     :discard)))

(re-frame/reg-event-fx
 :keymap/layout.upload
 (fn [cofx _]
   {:keymap/layout.upload (get-in cofx [:db :keymap/layout])}))

(defn change-key!
  [row col new-key]
  (re-frame/dispatch [:keymap/change-key! row col new-key]))

(defn layout []
  @(re-frame/subscribe [:keymap/layout]))

(defn layout:update! []
  (re-frame/dispatch [:keymap/layout.update]))

(defn layout:upload! []
  (re-frame/dispatch [:keymap/layout.upload]))

(defn switch-layer [layer]
  (re-frame/dispatch [:keymap/switch-layer layer]))

(re-frame/reg-event-fx
 :keymap/switch-layer
 (fn [{db :db :as cofx} [_ layer]]
   {:db (assoc db :keymap/layer layer)}))

(defn layer []
  @(re-frame/subscribe [:keymap/layer]))

(re-frame/reg-sub
 :keymap/layer
 (fn [db _]
   (if-let [layer (:keymap/layer db)]
     layer
     1)))

;;; ---- Live update ---- ;;;
(re-frame/reg-event-db
  :keymap/live-update
  (fn [db [_ live?]]
    (assoc db :keymap/live-update live?)))

(re-frame/reg-sub
  :keymap/live-update
  (fn [db _]
    (:keymap/live-update db)))

(defn live-update? []
  @(re-frame/subscribe [:keymap/live-update]))

(defn live-update!
  [live?]
  (re-frame/dispatch [:keymap/live-update live?]))
