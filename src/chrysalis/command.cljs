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

(ns chrysalis.command
  (:require [clojure.string :as s]
            [re-frame.core :as re-frame]

            [chrysalis.command.post-process :as post-process]))

;;; ---- re-frame events & fxes ---- ;;;

(re-frame/reg-event-db
 :command/queue
 (fn [db [_ device command args event]]
   (update db :command/queue conj [device command args event])))

(re-frame/reg-fx
 :command/send
 (fn [[device command args event]]
   (let [wire-command (str (name command) " " args "\n")]
     (.write (:port device) wire-command
             (fn []
               (.drain (:port device) (fn []
                                        (re-frame/dispatch [:command/queue device command args event]))))))))

(re-frame/reg-event-fx
 :command/send
 (fn [{db :db :as cofx} [_ command args event]]
   {:command/send [(:device/current db) command args event]}))


(re-frame/reg-event-fx
 :command/input.data
 (fn [cofx [_ data]]
   {:db (update (:db cofx) :command/input.buffer str data)
    :dispatch-later [{:ms 200
                      :dispatch [:command/input.process]}]}))

(defn- split-buffer [buffer]
  (loop [responses []
         buffer buffer
         index (.indexOf buffer ".\r\n")]
    (if (>= index 0)
      (let [remain (.substring buffer (+ index 3))]
        (recur (conj responses (-> buffer
                                   (.substring 0 index)
                                   s/trim))
               remain
               (.indexOf remain ".\r\n")))
      [responses buffer])))

(re-frame/reg-event-fx
 :command/input.process
 (fn [cofx _]
   (let [db (:db cofx)
         buffer (:command/input.buffer db)
         queue (:command/queue db)
         [responses remain] (split-buffer buffer)]
     {:dispatch-n (concat
                   (map (fn [[device cmd args event] response]
                          [event [device cmd args response]])
                        queue (reverse responses))
                   (map (fn [[device cmd args event] response]
                          [:command/history.append [device cmd args response]])
                        queue (reverse responses))
                   [[:command/input.clear (count responses) remain]])})))

(re-frame/reg-event-db
 :command/input.clear
 (fn [db [_ drop-from-queue remain]]
   (assoc (update db :command/queue #(drop drop-from-queue %))
          :command/input.buffer remain)))

(re-frame/reg-sub
 :command/history
 (fn [db _]
   (:command/history db)))

(re-frame/reg-event-db
 :command/history.append
 (fn [db [_ item]]
   (update db :command/history #(->> (conj % item)
                                     (take-last 50)))))

(re-frame/reg-sub
  :command/pending
  (fn [db _]
    (:command/queue db)))

;;; ---- API ---- ;;;
(defn history-append! [item]
  (re-frame/dispatch [:command/history.append item]))

(defn run [command args event]
   (re-frame/dispatch [:command/send command args event]))

(defn history
  ([] (history nil))
  ([processor] (let [items @(re-frame/subscribe [:command/history])]
                 (for [[device command args response] items]
                   (let [proc (if processor
                                (partial processor command)
                                identity)]
                     [device command args (proc response)])))))

(defn pending
  []
  @(re-frame/subscribe [:command/pending]))

;;; `on-data` was initially defined as an API function.
;;; However, given that it's only called when data comes in through
;;; a device port following a command sent via `run` maybe it should
;;; be considered a helper function.

(defn on-data [data]
  (re-frame/dispatch [:command/input.data data]))
