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

(ns chrysalis.device
  (:refer-clojure :exclude [list])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [chrysalis.hardware :as hardware]

            [reagent.ratom :as rv]
            [hickory.core :as h]
            [re-frame.core :as re-frame]
            [cljs.core.async :refer [<!]]))

;;; ---- re-frame ---- ;;;

;; I/O

(re-frame/reg-fx
 :device/open
 (fn [device]
   (re-frame/dispatch [:device/opened (hardware/open (:comName device))])))

(re-frame/reg-event-fx
 :device/open.current
 (fn [cofx _]
   {:device/open (get-in cofx [:db :device/current])}))

(re-frame/reg-fx
 :device/close
 (fn [device]
   (when-let [port (:port device)]
     (re-frame/dispatch [:device/closed])
     (.close port))))

(re-frame/reg-event-db
 :device/opened
 (fn [db [_ port]]
   (assoc-in db [:device/current :port] port)))

(re-frame/reg-event-db
 :device/closed
 (fn [db _]
   (assoc-in db [:device/current :port] nil)))

(re-frame/reg-event-db
 :files/add
 (fn [db [_ file-name data]]
   (assoc-in db [:files file-name] data)))

(re-frame/reg-sub-raw
 :device/svg
 (fn [app-db [_ file-name]]
   (when file-name
     (let [fs (js/require "fs")]
      (.readFile fs file-name "utf8"
                 (fn [err data]
                   (when-not err
                     (let [svg (-> data
                                   h/parse
                                   h/as-hiccup
                                   first
                                   (nth 3)
                                   (nth 2))]
                       (re-frame/dispatch [:files/add file-name svg])))))))
   (rv/make-reaction
    (fn [] (get-in @app-db [:files file-name])))))

;; Scanning

(re-frame/reg-fx
 :device/scan
 (fn []
   (let [in (hardware/detect (hardware/scan))]
     (go-loop [device (<! in)
               devices []]
       (if (= device {})
         (re-frame/dispatch [:device/scan-finished devices])
         (recur (<! in)
                (conj devices device)))))))

(re-frame/reg-event-fx
 :device/scan-finished
 (fn [cofx [_ devices]]
   {:db (assoc (:db cofx) :device/list devices)}))

(re-frame/reg-event-fx
 :device/scan
 (fn [cofx _]
   {:device/scan true}))

;; Listing

(re-frame/reg-sub
 :device/list
 (fn [db _]
   (:device/list db)))

(re-frame/reg-sub
 :device/current
 (fn [db _]
   (:device/current db)))

;; Selecting

(re-frame/reg-event-fx
 :device/select
 (fn [cofx [_ device]]
   {:db (assoc (:db cofx)
               :device/current device)}))

(re-frame/reg-event-db
 :device/select-by-serial
 (fn [db [_ serial]]
   (assoc db
          :device/current (->> (:device/list db)
                               (filter #(= (:serialNumber %) serial))
                               first))))

;;; ---- API ---- ;;;

(defn detect! []
  (re-frame/dispatch [:device/scan]))

(defn list []
  @(re-frame/subscribe [:device/list]))

(defn current []
  @(re-frame/subscribe [:device/current]))

(defn select! [id]
  (re-frame/dispatch [:device/select id]))

(defn select-by-serial! [serial]
  (re-frame/dispatch [:device/select-by-serial serial]))
