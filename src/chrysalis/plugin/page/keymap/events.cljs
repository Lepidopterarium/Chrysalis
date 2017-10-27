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
            [clojure.string :as s]
            [chrysalis.key :as key]
            [chrysalis.device :as device]))

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

(defn merge-edits
  [{edits :keymap/layout.edits layout :keymap/layout :as db}]
  (reduce (fn [layout [[layer index :as path] key]]
            (assoc-in layout path key))
          layout
          edits))

(re-frame/reg-sub
 :keymap/layout
 (fn [db _]
   (merge-edits db)))

(re-frame/reg-sub
  :keymap/layout.key
  ;; Get the current committed binding for the given key
  (fn [db [_ layer index]]
    (get-in db [:keymap/layout.edits [layer index]]
            (get-in db [:keymap/layout layer index]))))

(re-frame/reg-sub
  :keymap/layout.key.current
  ;; Get the current committed binding for the given key
  (fn [db [_ layer index]]
    (get-in db [:keymap/layout layer index])))

(re-frame/reg-sub
  :keymap/layout.edits
  (fn [db]
    (:keymap/layout.edits db)))

(re-frame/reg-event-db
 :keymap/layout.process
 (fn [db [_ [_ _ _ response]]]
   ;; TODO: make the response include the layer it's the map for to
   ;; avoid race conditions
   (let [layer (dec (or (:keymap/layer db) 1))]
     (assoc-in db [:keymap/layout layer]
               (post-process/format :keymap.layer response)))))

(defn- empty-layer
  [device]
  (let [keys-per-layer (if-let [keymap-layout (get-in device [:keymap :map])]
                         (reduce + 0 (mapcat count keymap-layout))
                         (->> (get-in device [:meta :matrix])
                             (apply *)))]
    (vec (repeat keys-per-layer {:plugin :core, :key :transparent}))))

(defn- pad-layouts
  "Add transparent layers to layout up to layer n"
  [layout n device]
  (reduce
    (fn [layout layer]
      (if (nil? (get layout layer))
        (assoc layout layer (empty-layer device))
        layout))
    layout
    (range (inc n))))

(re-frame/reg-event-fx
 :keymap/load-preset
 (fn [{db :db :as cofx} [_ layout]]
   (let [cur-layer (dec (or (:keymap/layer db) 1))
         changes (into
                   {}
                   (map-indexed (fn [idx key] [[cur-layer idx] key]))
                   layout)]
     {:db (-> db
              (update :keymap/layout.edits merge changes)
              (update :keymap/layout pad-layouts cur-layer (:device/current db)))})))

(re-frame/reg-fx
 :keymap/layout
 (fn [layer]
   (prn "layer " layer)
   (command/run :keymap.layer layer :keymap/layout.process)))

(re-frame/reg-event-fx
  :keymap/change-key!
  (fn [{db :db} [_ layer index new-key]]
    (-> {:db (-> db
                 (assoc-in [:keymap/layout.edits [layer index]] new-key)
                 (update :keymap/layout pad-layouts layer (:device/current db)))}
        (cond->
          (:keymap/live-update db)
          (assoc :dispatch [:keymap/layout.upload])))))

(re-frame/reg-event-fx
  :keymap/layout.update
  (fn [cofx _]
    {:keymap/layout (dec (or (get-in cofx [:db :keymap/layer]) 1))
     :db (assoc (:db cofx) :keymap/layout.edits {})}))

(re-frame/reg-fx
 :keymap/layout.upload
 (fn [[layer layout]]
   (command/run :keymap.layer
     (str layer
          " "
          (->> (get layout layer) (map key/unformat) (s/join " ")))
     :keymap/layout.update)))

(re-frame/reg-event-fx
 :keymap/layout.upload
 (fn [{db :db} _]
   (let [new-layout (merge-edits db)
         current-layer (dec (or (:keymap/layer db) 1))]
     {:keymap/layout.upload [current-layer new-layout]
      :db (assoc db
                 :keymap/layout new-layout
                 :keymap/layout.edits {})})))

(re-frame/reg-event-fx
  :keymap/layout.reset
  (fn [{db :db} _]
    ;; TODO: Update current layer after this runs
    {:keymap/layout.upload (repeat 5 (empty-layer (:device/current db)))}))

(defn change-key!
  [row col new-key]
  (re-frame/dispatch [:keymap/change-key! row col new-key]))

(defn layout []
  @(re-frame/subscribe [:keymap/layout]))

(defn layout-edits []
  @(re-frame/subscribe [:keymap/layout.edits]))

(defn saved-layout-key
  "Get the current binding for the key on layer `layer` at index
  `index`, ignoring pending edits."
  [layer index]
  @(re-frame/subscribe [:keymap/layout.key.current layer index]))

(defn layout-key
  [layer index]
  @(re-frame/subscribe [:keymap/layout.key layer index]))

(defn layout:update! []
  (re-frame/dispatch [:keymap/layout.update]))

(defn layout:upload! []
  (re-frame/dispatch [:keymap/layout.upload]))

(defn layout:reset! []
  (re-frame/dispatch [:keymap/layout.reset]))

(defn switch-layer [layer]
  (re-frame/dispatch [:keymap/switch-layer layer]))

(re-frame/reg-event-fx
 :keymap/switch-layer
 (fn [{db :db :as cofx} [_ layer]]
   {:db (assoc db :keymap/layer layer)
    :keymap/layout (dec layer)}))

(defn layer []
  @(re-frame/subscribe [:keymap/layer]))

(re-frame/reg-sub
 :keymap/layer
 (fn [db _]
   (if-let [layer (:keymap/layer db)]
     layer
     1)))

;;; ---- Live update ---- ;;;
(re-frame/reg-event-fx
  :keymap/live-update
  (fn [{db :db} [_ live?]]
    (-> {:db (assoc db :keymap/live-update live?)}
        (cond->
            (and live? (seq (:keymap/layout.edits db)))
          ;; if switching to live, commit existing edits
          (assoc :dispatch [:keymap/layout.upload])))))

(re-frame/reg-sub
  :keymap/live-update
  (fn [db _]
    (:keymap/live-update db)))

(defn live-update? []
  @(re-frame/subscribe [:keymap/live-update]))

(defn live-update!
  [live?]
  (re-frame/dispatch [:keymap/live-update live?]))

;;; --- Key editing tabs --- ;;;

(re-frame/reg-event-fx
  :keymap/add-edit-tab
  (fn [{db :db} [_ {title :title :as tab}]]
    {:db (-> (assoc-in db [:keymap/edit-tabs title] tab)
             (cond->
                 (nil? (get-in db [:keymap/edit-tabs title]))
               (update :keymap/edit-tabs-order (fnil conj []) title)))}))

(re-frame/reg-event-fx
  :keymap/remove-edit-tab
  (fn [{db :db} [_ title]]
    {:db (-> db
             (update :keymap/edit-tabs dissoc title)
             (update :keymap/edit-tabs-order
                     (partial filterv #(not= title %))))}))

(re-frame/reg-sub
  :keymap/edit-tabs
  (fn [db _]
    (mapv (:keymap/edit-tabs db) (:keymap/edit-tabs-order db))))

(defn add-edit-tab!
  [tab]
  (re-frame/dispatch [:keymap/add-edit-tab tab]))

(defn remove-edit-tab!
  [title]
  (re-frame/dispatch [:keymap/remove-edit-tab title]))

(defn edit-tabs
  []
  @(re-frame/subscribe [:keymap/edit-tabs]))

;; add default tabs

(defn- keys-like
  "Helper function for selecting groups of keys by regex of their name"
  [re]
  (into []
        (comp (remove nil?)
              (filter (fn [{k :key}] (some->> k name (re-matches re)))))
        key/HID-Codes))

;; TODO: is this the best place to do this?
(add-edit-tab!
  {:title "Alphanumeric"
   :keys (keys-like #"\d|\w")
   :modifiers? true})

(add-edit-tab!
  {:title "Punctuation & Spaces"
   :modifiers? true
   :keys
   (into []
         (comp (drop-while #(not= :enter (:key %)))
               (take-while #(not= :F1 (:key %))))
         key/HID-Codes)})

(add-edit-tab!
  {:title "Modifiers"
   ;; Should modifier keys be able to have additional modifiers on
   ;; them? Does this make sense? Do they need to have their own
   ;; modifier added?
   :modifiers? false
   :keys (keys-like #"(left|right)-(control|shift|alt|gui)")})

(add-edit-tab!
  {:title "Function"
   :modifiers? true
   :keys (keys-like #"F\d+")})

(add-edit-tab!
  {:title "Keypad"
   :modifiers? true
   :keys (keys-like #"keypad_.*")})
