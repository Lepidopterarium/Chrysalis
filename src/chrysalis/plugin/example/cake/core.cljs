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

(ns chrysalis.plugin.example.cake.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! close!]]
            [reagent.core :refer [atom]]
            [clojure.string :as s]

            [chrysalis.command :as command :refer [run]]))

(def palette {:off [0 0 0]
              :chocolate [123 63 0]
              :white [255 255 255]
              :yellow [255 255 0]})

(defn theme [colors]
  (s/join " "
          (map (fn [rgb] (s/join " " rgb))
               (map palette colors))))

(def cake [[:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :yellow
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :yellow
            :off :off :off :off]

           ;; ---
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :yellow
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :yellow
            :off :off :off :off]

           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :yellow
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :yellow
            :off :off :off :off]

           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :yellow
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :yellow
            :off :off :off :off]

           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :yellow
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :yellow
            :off :off :off :off]

           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :yellow
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :off :white :chocolate :chocolate
            :chocolate :chocolate :off :yellow
            :off :off :off :off]

           ;; END
           [:off :off :off :off
            :off :off :off :off
            :chocolate :chocolate :off :off
            :yellow :white :chocolate :chocolate
            :chocolate :chocolate :off :off
            :off :off :off :off]])

(defn- run-animation [device animation]
  (doall (map (fn [frame index]
                (.setTimeout js/window
                             (fn []
                               (run device :led.theme
                                 (theme frame)))
                             (* index 1000)))
              animation (range))))

(defmethod run :cake [device _ & _]
  (run-animation device cake)
  (atom "Boldog születésnapot!"))
