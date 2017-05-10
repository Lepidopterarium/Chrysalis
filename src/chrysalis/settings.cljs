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
  (:require [clojure.string :as s]
            [reagent.core :as reagent :refer [atom]]

            [chrysalis.core :as core]))

(def config-file (str (.getPath (.-app (.-remote (js/require "electron")))
                                "userData")
                      "/settings.json"))

(def hooks (atom {}))
(def data (atom {}))

(defn save!
  ([component]
   (let [hook-fns (component @hooks)]
     ((:save hook-fns)))
   (let [fs (js/require "fs")]
     (.writeFileSync fs config-file (.stringify js/JSON (clj->js @data))
                     #js {"mode" 0644})))
  ([]
   (doall (map (fn [[name hooks]]
                 ((:save hooks)))
               @hooks))
   (let [fs (js/require "fs")]
     (.writeFileSync fs config-file (.stringify js/JSON (clj->js @data))
                     #js {"mode" 0644}))))

(defn load! []
  (let [fs (js/require "fs")]
    (when (.existsSync fs config-file)
      (let [contents (js->clj (.parse js/JSON (.readFileSync fs config-file #js {"encoding" "utf-8"}))
                              :keywordize-keys true)]
        (reset! data contents))))
  (doall (map (fn [[name hooks]]
                ((:load hooks)))
              @hooks))
  nil)
