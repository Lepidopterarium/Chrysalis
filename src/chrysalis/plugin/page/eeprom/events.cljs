(ns chrysalis.plugin.page.eeprom.events
  (:require [re-frame.core :as re-frame]
            [chrysalis.command :as command]
            [clojure.string :as string]))

;;; Events

(re-frame/reg-event-db
  :eeprom/contents.process
  (fn [db [_ [_ _ _ response]]]
    (->> (string/split response #" ")
        (map #(js/parseInt % 10))
        (assoc db :eeprom/contents))))

(re-frame/reg-event-db
  :eeprom/space.process
  (fn [db [_ [_ _ _ response]]]
    (assoc db :eeprom/space (js/parseInt response 10))))

;;; Subscriptions

(re-frame/reg-sub
  :eeprom/space
  (fn [db _]
    (:eeprom/space db)))

(re-frame/reg-sub
  :eeprom/contents
  (fn [db _]
    (:eeprom/contents db)))

(defn free-space
  []
  @(re-frame/subscribe [:eeprom/space]))

(defn contents
  []
  @(re-frame/subscribe [:eeprom/contents]))

;;; Effects

(re-frame/reg-fx
  :eeprom/contents
  (fn []
    (command/run :eeprom.contents nil :eeprom/contents.process)))

(re-frame/reg-fx
  :eeprom/space
  (fn []
    (command/run :eeprom.free nil :eeprom/space.process)))
