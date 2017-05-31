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

(ns chrysalis.plugin.page.repl.events
  (:require [chrysalis.command :as command]

            [clojure.string :as s]
            [re-frame.core :as re-frame]))

;;; ---- History ---- ;;;

(re-frame/reg-sub
 :repl/history
 (fn [db _]
   (:repl/history db)))

(re-frame/reg-event-db
 :repl/history.append
 (fn [db [_ item]]
   (update db :repl/history #(->> (conj % item)
                                  (take-last 50)))))

(re-frame/reg-event-db
 :repl/history.clear
 (fn [db _]
   (dissoc db :repl/history)))

(defn history
  ([] (history nil))
  ([processor] (let [items @(re-frame/subscribe [:repl/history])]
                 (for [[device command args response] items]
                   (let [proc (if processor
                                (partial processor command)
                                identity)]
                     [device command args (proc response)])))))

(defn history:append! [item]
  (re-frame/dispatch [:repl/history.append item]))

(defn history:clear! []
  (re-frame/dispatch [:repl/history.clear]))

;; Navigation

(re-frame/reg-event-fx
 :repl/history.previous
 (fn [cofx _]
   (let [history-length (count (history))
         current-index (max (or (get-in cofx [:db :repl/history.index]) 0) 0)]

     (when (< current-index history-length)
       (let [[_ cmd args _] (nth (history) current-index)]
         {:db (assoc (:db cofx)
                     :repl/history.index (min (inc current-index) history-length))
          :dispatch [:repl/input.change (s/join " " [(name cmd) args])]})))))

(re-frame/reg-event-fx
 :repl/history.next
 (fn [cofx _]
   (let [history-length (count (history))
         current-index (min (or (get-in cofx [:db :repl/history.index]) 0) (- history-length 2))]
     (if (>= current-index 0)
       (let [[_ cmd args _] (nth (history) current-index)]
         {:db (assoc (:db cofx)
                     :repl/history.index (max (dec current-index) -1))
          :dispatch [:repl/input.change (s/join " " [(name cmd) args])]})
       {:dispatch [:repl/input.change nil]}))))

(defn history:previous! []
  (re-frame/dispatch [:repl/history.previous]))

(defn history:next! []
  (re-frame/dispatch [:repl/history.next]))

;;; ---- REPL Input Box ---- ;;;

(re-frame/reg-event-db
 :repl/input.change
 (fn [db [_ new-input]]
   (assoc db :repl/input new-input)))

(re-frame/reg-sub
 :repl/input
 (fn [db _]
   (:repl/input db)))

(defn input []
  @(re-frame/subscribe [:repl/input]))

(defn input! [new-input]
  (re-frame/dispatch [:repl/input.change new-input]))

(defn input:clear! []
  (re-frame/dispatch [:repl/input.change nil]))

;;; ---- Command I/O ---- ;;;

(re-frame/reg-fx
 :repl/command.send
 (fn [cmd]
   (let [[command & args] (.split cmd #" +")
         full-args (s/join " " (map (fn [arg]
                              (if (= (first arg) ":")
                                (.substring arg 1)
                                arg))
                            args))]
     (command/run (keyword command) full-args :repl/history.append))))

(re-frame/reg-event-fx
 :repl/command.send
 (fn [cofx _]
   {:dispatch [:repl/input.change nil]
    :repl/command.send (-> cofx :db :repl/input)}))

(re-frame/reg-event-fx
 :repl/command.run
 (fn [cofx [_ cmd]]
   {:repl/command.send cmd}))

(defn command:send! []
  (re-frame/dispatch [:repl/command.send]))

(defn command:run! [cmd]
  (re-frame/dispatch [:repl/command.run cmd]))
