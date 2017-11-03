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

;;; ---- Palette --- ;;;

(re-frame/reg-sub
  :led/palette
  (fn [db _]
    (:led/palette db)))

(re-frame/reg-sub
  :led/palette-selected
  (fn [db _]
    (:led/palette-selected db)))

(re-frame/reg-event-db
  :led/palette-selected!
  (fn [db [_ selected-idx]]
    (assoc db :led/palette-selected selected-idx)))

(defn current-palette-target []
  @(re-frame/subscribe [:led/palette-selected]))

(defn current-palette-target! [idx]
  (re-frame/dispatch [:led/palette-selected! idx]))

(re-frame/reg-event-db
  :led/palette.process
  (fn [db [_ [_ _ _ response]]]
    (assoc db :led/palette (post-process/format :led.palette response))))

(re-frame/reg-fx
  :led/palette
  (fn [_]
    (command/run :palette nil :led/palette.process)))

(re-frame/reg-fx
  :led/palette.upload
  (fn [palette]
    (command/run :palette (->> palette flatten (s/join " ")) :discard)))

(re-frame/reg-event-fx
  :led/palette.upload
  (fn [{db :db} _]
    {:led/palette! (db :led/palette)}))

(re-frame/reg-event-fx
  :led/palette.update
  (fn [cofx _]
    {:led/palette nil}))

(re-frame/reg-event-fx
  :led/palette!
  (fn [cofx [_ palette]]
    (let [live? (get-in (:db cofx) [:led/live-update])]
      (-> {:db (assoc (:db cofx) :led/palette palette)}
          (cond->
              live? (assoc :led/palette.upload palette))))))

(defn palette []
  @(re-frame/subscribe [:led/palette]))

(defn palette:update! []
  (re-frame/dispatch [:led/palette.update]))

(defn palette:upload! []
  (re-frame/dispatch [:led/palette.upload]))

;;; ---- Theme ---- ;;;

(re-frame/reg-sub
 :led/theme
 (fn [db _]
   (:led/theme db)))

(re-frame/reg-event-db
 :led/theme.process
 (fn [db [_ [_ _ _ response]]]
   (assoc db :led/theme (post-process/format :led.theme response))))

(re-frame/reg-event-fx
 :led/theme!
 (fn [cofx [_ theme]]
   (let [live? (get-in (:db cofx) [:led/live-update])]
     (-> {:db (assoc (:db cofx) :led/theme theme)}
         (cond->
             live? (assoc :led/theme.upload theme))))))

(re-frame/reg-fx
 :led/theme
 (fn []
   (command/run :led.theme nil :led/theme.process)))

(re-frame/reg-event-fx
 :led/theme.update
 (fn [cofx _]
   {:led/theme :update}))

(re-frame/reg-fx
 :led/theme.upload
 (fn [theme]
   (command/run :led.theme (->> theme
                                flatten
                                (s/join " ")) :discard)))

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

;;; ---- Live update ---- ;;;
(re-frame/reg-event-db
 :led/live-update
 (fn [db [_ v]]
   (assoc db :led/live-update v)))

(re-frame/reg-sub
 :led/live-update
 (fn [db _]
   (:led/live-update db)))

(defn live-update? []
  @(re-frame/subscribe [:led/live-update]))

(defn live-update! [v]
  (re-frame/dispatch [:led/live-update v]))
