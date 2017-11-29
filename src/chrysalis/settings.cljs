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
  (:require [re-frame.core :as re-frame]
            [cljs.reader :as edn]))

(defonce config-file (str (.getPath (.-app (.-remote (js/require "electron")))
                                    "userData")
                          "/settings.edn"))

(re-frame/reg-cofx
 :settings/load
 (fn [cofx _]
   (let [fs (js/require "fs")]
     (when (.existsSync fs config-file)
       (let [contents (-> fs
                        (.readFileSync config-file #js {"encoding" "utf-8"})
                        edn/read-string)]
         (assoc cofx :settings contents))))))

(re-frame/reg-fx
 :settings/save
 (fn [settings]
   (let [fs (js/require "fs")]
     (.writeFileSync fs config-file
                     (pr-str settings)
                     #js {"mode" 0644}))))

(def ^:private settings-file-filters
  #js [ #js {"name" "EDN files"
             "extensions" #js ["edn"]}])

(re-frame/reg-fx
  :settings/export
  (fn [to-export]
    (let [dialog (.. (js/require "electron") -remote -dialog)
          fs (js/require "fs")]
      (.showSaveDialog
        dialog
        #js {"title" "Export Chrysalis Setting"
             "defaultPath" "export.edn"
             "filters" settings-file-filters}
        (fn [file-name]
          (when file-name
            (.writeFileSync fs file-name
                            (pr-str to-export)
                            #js {"mode" 0644})))))))

(re-frame/reg-fx
  :settings/import
  (fn [_]
    (let [dialog (.. (js/require "electron") -remote -dialog)
          fs (js/require "fs")]
      (.showOpenDialog
        dialog
        #js {"title" "Load Chrysalis Setting"
             "filters" settings-file-filters
             "properties" #js ["openFile" "multiSelections"]}
        (fn [file-names]
          (doseq [file file-names]
            (.readFile fs file
                       #js {"encoding" "utf-8"}
                       (fn [err data]
                         (when-not err
                           (->> data
                               edn/read-string
                               (vector :settings/-merge-imported)
                               (re-frame/dispatch)))))))))))

(re-frame/reg-event-fx
  :settings/import
  (fn [_ _]
    {:settings/import nil}))

(defmulti apply!
  (fn [_ page]
    [page]))

(re-frame/reg-event-fx
  :settings/-merge-imported
  (fn [{db :db} [_ imported]]
    (let [settings (assoc-in (:settings db) (:path imported)
                             (:value imported))
          cur-page (:page/current db)]
      {:db (-> db (assoc :settings settings)
               (apply! cur-page))
       :settings/save settings})))

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
               (assoc-in context [:effects :db] (save! db page))))
   :after (fn [context]
            (let [{:keys [db]} (:effects context)
                  [_ page] (-> context :coeffects :event)]
              (assoc-in context [:effects :db] (apply! db page))))))
