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
            [reagent.core :refer [atom]]

            [chrysalis.core :refer [state]]
            [chrysalis.key :as key]))

(defn on-data [data]
  (swap! state update-in [:command :result :buffer] str data)
  (let [buff (get-in @state [:command :result :buffer])
        commandEnd (.indexOf buff ".\r\n")]
    (when (>= commandEnd 0)
      (let [[result callback] (first (get-in @state [:command :queue]))]
        (callback result (.trim (.substring buff 0 commandEnd)))
        (swap! state update-in [:command :spy] concat [(.substring buff 0 (+ commandEnd 5))])
        (swap! state update-in [:command :result :buffer]
               (fn [old]
                 (.substring old (+ commandEnd 3))))
        (swap! state update-in [:command :queue]
               (fn [old]
                 (rest old)))))))

(defn send* [device command callback]
  (let [result (atom nil)
        wire-command (str command "\n")]
    (swap! state update-in [:command :spy] concat [wire-command])
    (.write device wire-command
            (fn []
              (.drain device (fn []
                               (swap! state update-in [:command :queue] concat [[result callback]])))))
    result))

(defn send [device command]
  (send* device command (fn [out text] (reset! out text))))

(defmulti process*
  (fn [command]
    (keyword command)))

(defmethod process* :default [_]
  (fn [result text]
    (reset! result text)))

(defmethod process* :version [_]
  (fn [result text]
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

(defmethod process* :help [_]
  (fn [result text]
    (let [lines (.split text #"\r?\n")]
      (reset! result (->> lines
                          (map keyword)
                          vec)))))

(defmethod process* :layer.getState [_ result]
  (fn [result text]
    (reset! result
            (->> (map (fn [state idx] [idx (= state "1")]) text (range))
                 (filter second)
                 (map first)
                 vec))))

(defmethod process* :keymap.map [_ result]
  (fn [result text]
    (reset! result
            (->> (.split (.trim text) #" ")
                 (map js/parseInt)
                 (map key/from-code)
                 vec))))

(defmulti pre-process*
  (fn [command args]
    (keyword command)))

(defmethod pre-process* :default [_ args]
  args)

(defmulti run
  (fn [_ command & _]
    command))

(defmethod run :default [device command & args]
  (if args
    (send* device
           (str (name command) " "
                (s/join " " (pre-process* command args)))
           (process* :none))
    (send* device (name command) (process* command))))
