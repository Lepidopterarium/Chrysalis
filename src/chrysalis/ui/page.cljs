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

(ns chrysalis.ui.page
  (:refer-clojure :exclude [list])
  (:require [re-frame.core :as re-frame]

            [chrysalis.settings :as settings]))

;;; ---- re-frame events & handlers ---- ;;;

(re-frame/reg-event-fx
 :page/add!
 (fn [cofx [_ key page-data]]
   {:db (assoc-in (:db cofx)
                  [:pages key]
                  page-data)}))

(re-frame/reg-sub
 :page/current
 (fn [db _]
   (assoc (get-in db [:pages (:page/current db)])
          :key (:page/current db))))

(re-frame/reg-sub
 :page/list
 (fn [db _]
   (:pages db)))

(re-frame/reg-event-fx
 :page/select
 [settings/interceptor]
 (fn [cofx [_ page]]
   (let [new-page (get-in (:db cofx) [:pages page])
         current-device (:device/current (:db cofx))]
     (->
      {:db (assoc (:db cofx)
                  :page/current page)
       :key-bindings/reset true
       :settings/save (get-in cofx [:db :settings])}

      (cond->
          (:device/need? new-page) (assoc :device/open current-device)
          (not (:device/need? new-page) (assoc :device/close current-device)))

      (merge (:events new-page))))))

;;; ---- helpers ---- ;;;

(defn add! [key page-data]
  (re-frame/dispatch [:page/add! key page-data]))

(defn current []
  @(re-frame/subscribe [:page/current]))

(defn list []
  @(re-frame/subscribe [:page/list]))

(defn switch-to! [page]
  (re-frame/dispatch [:page/select page]))
