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

(ns chrysalis.key-bindings
  (:require [re-frame.core :as re-frame]
            [chrysalis.ui.page :as page]))

(defonce mousetrap (js/require "mousetrap"))

(re-frame/reg-fx
 :key-bindings/reset
 (fn [_]
   (.reset mousetrap)

   ;; Apply global key bindings...
   (let [pages (map-indexed vector (sort-by (fn [[key meta]]
                                              (:index meta))
                                            (page/list)))]
     (doall
      (for [[index [key meta]] pages]
        (.bind mousetrap (str "alt+" index)
               (fn [& _]
                 (when-not (and (:disable? meta)
                                ((:disable? meta)))
                   (page/switch-to! key)))))))))

(re-frame/reg-fx
 :key-bindings/add
 (fn [[key action]]
   (.bind mousetrap key action)))

(re-frame/reg-event-fx
 :key-bindings/add
 (fn [cofx [_ key action]]
   {:key-bindings/add [key action]}))

(defn add! [key action]
  (re-frame/dispatch [:key-bindings/add key action]))
