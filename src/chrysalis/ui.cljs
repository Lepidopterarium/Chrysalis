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

(ns chrysalis.ui
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan <! >! close!]]
            [clojure.string :as s]
            [reagent.core :refer [atom]]))

(defn repl-wrap [req key result]
  [:div.row {:style {:margin-bottom "1em"}
             :key key}
   [:pre.col-sm-12 {:style {:white-space :pre-wrap}}
    "❯ " [:b req] "\n"
    (if-not (= result "\"\"")
      result
      [:i "<no output>"])]])

(defmulti display
  (fn [command _ _ _]
    command))

(defmethod display :default [_ req result key]
  (when result
    (repl-wrap req key
               (.stringify js/JSON (clj->js result) nil 2))))
