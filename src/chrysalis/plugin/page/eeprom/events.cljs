(ns chrysalis.plugin.page.eeprom.events
  (:require [re-frame.core :as re-frame]
            [chrysalis.command :as command]
            [clojure.string :as string]))

;;; Events

(re-frame/reg-event-db
  :eeprom/contents.process
  (fn [db [_ [_ _ _ response]]]
    (->> (string/split response #" ")
        (assoc db :eeprom/contents))))

(re-frame/reg-event-db
  :eeprom/space.process
  (fn [db [_ [_ _ _ response]]]
    (assoc db :eeprom/space (js/parseInt response 10))))

;;; Effects

(re-frame/reg-fx
  :eeprom/contents
  (fn []
    (command/run :eeprom.contents nil :eeprom/contents.process)))

(re-frame/reg-fx
  :eeprom/space
  (fn []
    (command/run :eeprom.free nil :eeprom/space.process)))
