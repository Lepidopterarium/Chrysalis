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

(ns chrysalis.plugin.page.led.core
  (:refer-clojure :exclude [slurp])
  (:require [chrysalis.core :refer [state pages]]
            [chrysalis.ui :refer [page color->hex]]
            [chrysalis.hardware :as hardware]
            [chrysalis.device :as device]
            [chrysalis.command :as command]

            [reagent.core :as reagent :refer [atom]]
            [hickory.core :as h]
            [clojure.walk :as walk]))

(defn- get-theme! []
  (let [port (hardware/open (get-in (device/current) [:device :comName]))
        theme (command/run port :led.theme)]
    (.setTimeout js/window #(.close port) 100)
    (swap! state assoc-in [:led :theme] theme)))

(defn- gamma-correct* [gamma-map color]
  (let [color-indexes (map first (filter #(= color (second %)) (map-indexed vector gamma-map)))
        new-color (int (last color-indexes))]
    (if (zero? color)
      color
      new-color)))

(defn gamma-correct [color]
  (if-let [gamma-map (get-in (device/current) [:device :led :gamma])]
    (gamma-correct* gamma-map color)
    color))

(defn- key-index [r c cols]
  (if-let [led-map (get-in (device/current) [:device :led :map])]
    (nth (nth led-map r) c)
    (+ (* r cols) c)))

(defn- key-color [node theme]
  (let [[r c] (map js/parseInt (rest (re-find #"R(\d+)C(\d+)_F" (:id node))))]
    (if (and r c)
      (let [[rows cols] (get-in (device/current) [:device :meta :matrix])
            index (key-index r c cols)
            color (nth theme index [0 0 0])]
        (assoc node :fill (color->hex (map gamma-correct color))))
      node)))

(defn with-colors [svg theme]
  (walk/prewalk (fn [node]
                  (if (and (map? node) (get node :id))
                    (key-color node theme)
                    node))
                svg))

(defn <led-theme> [file-name theme props]
  (let [fs (js/require "fs")
        result (atom nil)]
    (.readFile fs file-name "utf8"
               (fn [err data]
                 (when-not err
                   (reset! result (-> data
                                      h/parse
                                      h/as-hiccup
                                      first
                                      (nth 3)
                                      (nth 2)
                                      (assoc 1 (assoc props :view-box "0 0 2048 1280")))))))
    (fn []
      (if @theme
        (with-colors @result @theme)
        [:i.fa.fa-refresh.fa-spin.fa-5x]))))

(defmethod page [:enter :led] [_ _]
  (get-theme!))

(defmethod page [:render :led] [_ _]
  [:div.container-fluid
   [:div.row.justify-content-center
    [:div.col-sm-12.text-center
     [:h2 "LED Theme Editor"]]]
   [:div.row
    [:div.col-sm-12.text-center
     [<led-theme> (get-in (device/current) [:device :meta :layout])
      (get-in @state [:led :theme])
      {:width 1024 :height 640}]]]])

(swap! pages assoc :led {:name "LED Theme Editor"
                         :index 10
                         :disable? (fn [] (nil? (device/current)))})
