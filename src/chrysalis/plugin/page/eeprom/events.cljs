(ns chrysalis.plugin.page.eeprom.events
  (:require [re-frame.core :as re-frame]
            [chrysalis.command :as command]
            [clojure.string :as string]))

;;; Effects

(re-frame/reg-fx
  :eeprom/contents
  (fn []
    (command/run :eeprom.contents nil :eeprom/contents.process)))

(re-frame/reg-fx
  :eeprom/space
  (fn []
    (command/run :eeprom.free nil :eeprom/space.process)))

(re-frame/reg-fx
  :eeprom/update
  (fn [new-contents]
    (command/run :eeprom.contents (string/join " " new-contents) :discard)))

;;; Events

(re-frame/reg-event-db
  :eeprom/contents.process
  (fn [db [_ [_ _ _ response]]]
    (->> (string/split response #" ")
        (mapv #(js/parseInt % 10))
        (assoc db :eeprom/contents))))

(re-frame/reg-event-db
  :eeprom/space.process
  (fn [db [_ [_ _ _ response]]]
    (assoc db :eeprom/space (js/parseInt response 10))))

(re-frame/reg-event-db
  :eeprom/set-selected-cell
  (fn [db [_ idx]]
    (assoc db :eeprom/selected-cell idx)))

(re-frame/reg-event-db
  :eeprom/set-cell
  (fn [db [_ idx val]]
    (assoc-in db [:eeprom/contents idx] val)))

(re-frame/reg-event-fx
  :eeprom/commit-changes
  (fn [{db :db} _]
    {:eeprom/update (:eeprom/contents db)}))

(defn set-selected!
  [idx]
  (re-frame/dispatch [:eeprom/set-selected-cell idx]))

(defn set-cell!
  [idx val]
  (re-frame/dispatch [:eeprom/set-cell idx val]))

(defn commit-eeprom-changes!
  []
  (re-frame/dispatch [:eeprom/commit-changes]))

;;; Subscriptions

(re-frame/reg-sub
  :eeprom/space
  (fn [db _]
    (:eeprom/space db)))

(re-frame/reg-sub
  :eeprom/contents
  (fn [db _]
    (:eeprom/contents db)))

(re-frame/reg-sub
  :eeprom/content-cell
  (fn [db [_ idx]]
    (get-in db [:eeprom/contents idx])))

(re-frame/reg-sub
  :eeprom/selected-index
  (fn [db _]
    (:eeprom/selected-cell db)))

(defn free-space
  []
  @(re-frame/subscribe [:eeprom/space]))

(defn contents
  []
  @(re-frame/subscribe [:eeprom/contents]))

(defn content-cell
  [idx]
  @(re-frame/subscribe [:eeprom/content-cell idx]))

(defn selected-index
  []
  @(re-frame/subscribe [:eeprom/selected-index]))
