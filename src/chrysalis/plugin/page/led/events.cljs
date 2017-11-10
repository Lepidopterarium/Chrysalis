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

;;; --- Current layer --- ;;;

(re-frame/reg-sub
  :led/layer
  (fn [db _]
    (or (:led/layer db) 1)))

(re-frame/reg-event-db
  :led/layer!
  (fn [db [_ new-layer]]
    (assoc db :led/layer new-layer)))

(defn layer []
  @(re-frame/subscribe [:led/layer]))

(defn switch-layer
  [new-layer]
  (re-frame/dispatch [:led/layer! new-layer]))

(defn- current-layer
  [db]
  (dec (or (:led/layer db) 1)))

;;; ---- Current target ------ ;;;

(re-frame/reg-sub
 :led/current-target
 (fn [db _]
   (:led/current-target db)))

(re-frame/reg-event-db
 :led/current-target
 (fn [db [_ new-target]]
   (let [layer (current-layer db)
         target-palette (get-in db [:led/colormap layer new-target])]
     (assoc db
            :led/current-target new-target
            :led/palette-selected target-palette))))

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
    {:led/palette.upload (db :led/palette)}))

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

;;; ---- Colormap ---- ;;;

(re-frame/reg-sub
  :led/colormap
  (fn [{:keys [led/layer led/palette led/colormap] :as db} _]
    (mapv palette (get colormap (dec (or layer 1))))))

(re-frame/reg-sub
  :led/colormap.at
  (fn [db [_ key-idx]]
    (let [layer (current-layer db)
          palette-idx (get-in db [:led/colormap layer key-idx])]
      (get-in db [:led/palette palette-idx]))))

(re-frame/reg-event-db
 :led/colormap.process
 (fn [db [_ [_ _ _ response]]]
   (assoc db :led/colormap (post-process/format :led.colormap response))))

(re-frame/reg-event-fx
  :led/colormap!
  (fn [cofx [_ colormap]]
    (let [live? (get-in (:db cofx) [:led/live-update])]
      (-> {:db (assoc (:db cofx) :led/colormap colormap)}
          (cond->
              live? (assoc :led/colormap.upload colormap))))))

(re-frame/reg-fx
  :led/colormap
  (fn []
    (command/run :colormap.map nil :led/colormap.process)))

(re-frame/reg-event-fx
  :led/colormap.update
  (fn [cofx _]
    {:led/colormap :update}))

(re-frame/reg-fx
 :led/colormap.upload
 (fn [colormap]
   (command/run :colormap.map
     (->> colormap flatten (s/join " ")) :discard)))

(re-frame/reg-event-fx
 :led/colormap.upload
 (fn [cofx _]
   {:led/colormap.upload (get-in cofx [:db :led/colormap])}))

(re-frame/reg-event-fx
  :led/colormap.update-at
  (fn [{db :db} [_ palette-idx]]
    (when-let [target-idx (:led/current-target db)]
      (let [layer (current-layer db)
            new-colormap (assoc-in (:led/colormap db) [layer target-idx] palette-idx)
            live? (db :led/live-update)]
        (-> {:db (assoc db :led/colormap new-colormap)}
            (cond->
                live? (assoc :led/colormap.upload new-colormap)))))))

(defn colormap []
  @(re-frame/subscribe [:led/colormap]))

(defn colormap-at [idx]
  @(re-frame/subscribe [:led/colormap-at idx]))

(defn colormap:update! []
  (re-frame/dispatch [:led/colormap.update]))

(defn colormap:upload! []
  (re-frame/dispatch [:led/colormap.upload]))

(defn colormap:set-target-color! [palette-idx]
  (re-frame/dispatch [:led/colormap.update-at palette-idx]))

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
