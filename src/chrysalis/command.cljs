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
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! close!]]
            [clojure.string :as s]
            [reagent.core :refer [atom]]))

(defn- drop-trailing-dot [s]
  (.substring s 0 (- (.-length s) 5)))

(defn send* [device command callback]
  (.write device (str command "\n")
          (fn []
            (.drain device (fn []
                             (.setTimeout js/window (fn []
                                                      (callback (drop-trailing-dot (str (.read device)))))
                                          100))))))

(defn send [device command]
  (let [result (atom nil)]
    (send* device command (fn [text] (reset! result text)))
    result))

(defmulti process*
  (fn [command _]
    (keyword command)))

(defmethod process* :default [_ result]
  (fn [text]
    (reset! result text)))

(defmethod process* :version [_ result]
  (fn [text]
    (let [[version-and-device date] (.split text #" \| ")
          t (.split version-and-device #" ")
          fwver (-> t
                    first
                    (.split #"/")
                    second)
          [manufacturer product] (-> t
                                     (.slice 1)
                                     (.join " ")
                                     (.split #"/"))]
      (reset! result {:device {:manufacturer manufacturer
                               :product product}
                      :firmware-version fwver
                      :timestamp date}))
    result))

(defmethod process* :help [_ result]
  (fn [text]
    (let [lines (.split text #"\r?\n")]
      (reset! result (->> lines
                          (map keyword)
                          vec)))))

(defmethod process* :layer.getState [_ result]
  (fn [text]
    (reset! result
            (->> (map (fn [state idx] [idx (= state "1")]) text (range))
                 (filter second)
                 (map first)
                 vec))))

(defmulti pre-process*
  (fn [command args]
    (keyword command)))

(defmethod pre-process* :default [_ args]
  args)

(defn run [device command & args]
  (let [result (atom nil)]
    (if args
      (send* device
             (str (name command) " "
                  (s/join " " (pre-process* command args)))
             (process* :none result))
      (send* device (name command) (process* command result)))
    result))
