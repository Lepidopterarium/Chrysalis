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

(ns chrysalis.settings
  (:require [re-frame.core :as re-frame]))

(defonce config-file (str (.getPath (.-app (.-remote (js/require "electron")))
                                    "userData")
                          "/settings.json"))

(re-frame/reg-cofx
 :settings/load
 (fn [cofx _]
   (let [fs (js/require "fs")]
     (when (.existsSync fs config-file)
       (let [contents (js->clj (as-> fs $
                                 (.readFileSync $ config-file #js {"encoding" "utf-8"})
                                 (.parse js/JSON $))
                               :keywordize-keys true)]
         (assoc cofx :settings contents))))))

(re-frame/reg-fx
 :settings/save
 (fn [settings]
   (let [fs (js/require "fs")]
     (.writeFileSync fs config-file
                     (->> settings
                          clj->js
                          (.stringify js/JSON))
                     #js {"mode" 0644}))))

(defmulti apply!
  (fn [_ page]
    [page]))

(defmethod apply! :default [db _]
  db)

(defn copy-> [db settings-path db-path]
  (assoc-in db db-path
         (get-in (:settings db) settings-path)))

(defn <-copy [db settings-path db-path]
  (assoc-in (:settings db) settings-path
            (get-in db db-path)))

(defmulti save!
  (fn [_ page]
    [page]))

(defmethod save! :default [db _]
  db)

(def interceptor
  (re-frame/->interceptor
   :id :settings/interceptor
   :before (fn [context]
             (let [db (-> context :coeffects :db)
                   page (:page/current db)]
               (assoc-in context [:effects :db]
                         (save! db page))))
   :after (fn [context]
            (let [{:keys [db]} (:effects context)
                  [_ page] (-> context :coeffects :event)]
              (assoc-in context [:effects :db]
                        (apply! db page))))))
