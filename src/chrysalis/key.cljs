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

(ns chrysalis.key
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as s]))

(def HID-Codes
  [{:type :no-key}
   {:type :error, :error :rollover}
   {:type :error, :error :post-fail}
   {:type :error, :error :undefined}
   {:type :keyboard, :usage :a}
   {:type :keyboard, :usage :b}
   {:type :keyboard, :usage :c}
   {:type :keyboard, :usage :d}
   {:type :keyboard, :usage :e}
   {:type :keyboard, :usage :f}
   {:type :keyboard, :usage :g}
   {:type :keyboard, :usage :h}
   {:type :keyboard, :usage :i}
   {:type :keyboard, :usage :j}
   {:type :keyboard, :usage :k}
   {:type :keyboard, :usage :l}
   {:type :keyboard, :usage :m}
   {:type :keyboard, :usage :n}
   {:type :keyboard, :usage :o}
   {:type :keyboard, :usage :p}
   {:type :keyboard, :usage :q}
   {:type :keyboard, :usage :r}
   {:type :keyboard, :usage :s}
   {:type :keyboard, :usage :t}
   {:type :keyboard, :usage :u}
   {:type :keyboard, :usage :v}
   {:type :keyboard, :usage :w}
   {:type :keyboard, :usage :x}
   {:type :keyboard, :usage :y}
   {:type :keyboard, :usage :z}
   {:type :keyboard, :usage :1}
   {:type :keyboard, :usage :2}
   {:type :keyboard, :usage :3}
   {:type :keyboard, :usage :4}
   {:type :keyboard, :usage :5}
   {:type :keyboard, :usage :6}
   {:type :keyboard, :usage :7}
   {:type :keyboard, :usage :8}
   {:type :keyboard, :usage :9}
   {:type :keyboard, :usage :0}
   {:type :keyboard, :usage :enter}
   {:type :keyboard, :usage :escape}
   {:type :keyboard, :usage :backspace}
   {:type :keyboard, :usage :tab}
   {:type :keyboard, :usage :space}
   {:type :keyboard, :usage :minus}
   {:type :keyboard, :usage :equals}
   {:type :keyboard, :usage :left-square-bracket}
   {:type :keyboard, :usage :right-square-bracket}
   {:type :keyboard, :usage :backslash}
   {:type :keyboard, :usage :non-US-pound}
   {:type :keyboard, :usage :semicolon}
   {:type :keyboard, :usage :quote}
   {:type :keyboard, :usage :backtick}
   {:type :keyboard, :usage :colon}
   {:type :keyboard, :usage :dot}
   {:type :keyboard, :usage :slash}
   {:type :keyboard, :usage :caps-lock}
   {:type :keyboard, :usage :F1}
   {:type :keyboard, :usage :F2}
   {:type :keyboard, :usage :F3}
   {:type :keyboard, :usage :F4}
   {:type :keyboard, :usage :F5}
   {:type :keyboard, :usage :F6}
   {:type :keyboard, :usage :F7}
   {:type :keyboard, :usage :F8}
   {:type :keyboard, :usage :F9}
   {:type :keyboard, :usage :F10}
   {:type :keyboard, :usage :F11}
   {:type :keyboard, :usage :F12}
   {:type :keyboard, :usage :print-screen}
   {:type :keyboard, :usage :scroll-lock}
   {:type :keyboard, :usage :pause}
   {:type :keyboard, :usage :insert}
   {:type :keyboard, :usage :home}
   {:type :keyboard, :usage :page-up}
   {:type :keyboard, :usage :delete}
   {:type :keyboard, :usage :end}
   {:type :keyboard, :usage :page-down}
   {:type :keyboard, :usage :right-arrow}
   {:type :keyboard, :usage :left-arrow}
   {:type :keyboard, :usage :down-arrow}
   {:type :keyboard, :usage :up-arrow}
   {:type :keyboard, :usage :num-lock}
   {:type :keypad, :usage :divide}
   {:type :keypad, :usage :multiply}
   {:type :keypad, :usage :minus}
   {:type :keypad, :usage :plus}
   {:type :keypad, :usage :enter}
   {:type :keypad, :usage :1}
   {:type :keypad, :usage :2}
   {:type :keypad, :usage :3}
   {:type :keypad, :usage :4}
   {:type :keypad, :usage :5}
   {:type :keypad, :usage :6}
   {:type :keypad, :usage :7}
   {:type :keypad, :usage :8}
   {:type :keypad, :usage :9}
   {:type :keypad, :usage :0}
   {:type :keypad, :usage :dot}
   {:type :keyboard, :usage :non-US-slash}
   {:type :keyboard, :usage :application}
   {:type :keyboard, :usage :power}
   {:type :keypad, :usage :equals}
   {:type :keyboard, :usage :F13}
   {:type :keyboard, :usage :F14}
   {:type :keyboard, :usage :F15}
   {:type :keyboard, :usage :F16}
   {:type :keyboard, :usage :F17}
   {:type :keyboard, :usage :F18}
   {:type :keyboard, :usage :F19}
   {:type :keyboard, :usage :F20}
   {:type :keyboard, :usage :F21}
   {:type :keyboard, :usage :F22}
   {:type :keyboard, :usage :F23}
   {:type :keyboard, :usage :F24}
   {:type :keyboard, :usage :execute}
   {:type :keyboard, :usage :help}
   {:type :keyboard, :usage :menu}
   {:type :keyboard, :usage :select}
   {:type :keyboard, :usage :stop}
   {:type :keyboard, :usage :again}
   {:type :keyboard, :usage :undo}
   {:type :keyboard, :usage :cut}
   {:type :keyboard, :usage :copy}
   {:type :keyboard, :usage :paste}
   {:type :keyboard, :usage :find}
   {:type :keyboard, :usage :mute}
   {:type :keyboard, :usage :volume-up}
   {:type :keyboard, :usage :volume-down}
   {:type :keyboard, :usage :locking-caps-lock}
   {:type :keyboard, :usage :locking-num-lock}
   {:type :keyboard, :usage :locking-scroll-lock}
   {:type :keypad, :usage :comma}
   {:type :keypad, :usage :equal-sign}
   {:type :keyboard, :usage :international-1}
   {:type :keyboard, :usage :international-2}
   {:type :keyboard, :usage :international-3}
   {:type :keyboard, :usage :international-4}
   {:type :keyboard, :usage :international-5}
   {:type :keyboard, :usage :international-6}
   {:type :keyboard, :usage :international-7}
   {:type :keyboard, :usage :international-8}
   {:type :keyboard, :usage :international-9}
   {:type :keyboard, :usage :lang-1}
   {:type :keyboard, :usage :lang-2}
   {:type :keyboard, :usage :lang-3}
   {:type :keyboard, :usage :lang-4}
   {:type :keyboard, :usage :lang-5}
   {:type :keyboard, :usage :lang-6}
   {:type :keyboard, :usage :lang-7}
   {:type :keyboard, :usage :lang-8}
   {:type :keyboard, :usage :lang-9}
   {:type :keyboard, :usage :alternate-erase}
   {:type :keyboard, :usage :sysrq}
   {:type :keyboard, :usage :cancel}
   {:type :keyboard, :usage :clear}
   {:type :keyboard, :usage :prior}
   {:type :keyboard, :usage :return}
   {:type :keyboard, :usage :separator}
   {:type :keyboard, :usage :out}
   {:type :keyboard, :usage :oper}
   {:type :keyboard, :usage :clear-or-again}
   {:type :keyboard, :usage :crsel-or-props}
   {:type :keyboard, :usage :exsel}
   nil ;; Reserved
   nil
   nil
   nil
   nil
   nil
   nil
   nil
   nil
   nil
   nil ;; Reserved
   {:type :keypad, :usage :00}
   {:type :keypad, :usage :000}
   {:type :keypad, :usage :thousands-separator}
   {:type :keypad, :usage :decimal-separator}
   {:type :keypad, :usage :currency-unit}
   {:type :keypad, :usage :currency-sub-unit}
   {:type :keypad, :usage :opening-parens}
   {:type :keypad, :usage :closing-parens}
   {:type :keypad, :usage :opening-curly-braces}
   {:type :keypad, :usage :closing-curly-brackes}
   {:type :keypad, :usage :tab}
   {:type :keypad, :usage :backspace}
   {:type :keypad, :usage :a}
   {:type :keypad, :usage :b}
   {:type :keypad, :usage :c}
   {:type :keypad, :usage :d}
   {:type :keypad, :usage :e}
   {:type :keypad, :usage :f}
   {:type :keypad, :usage :xor}
   {:type :keypad, :usage :caret}
   {:type :keypad, :usage :percent}
   {:type :keypad, :usage :<}
   {:type :keypad, :usage :>}
   {:type :keypad, :usage :&}
   {:type :keypad, :usage :&&}
   {:type :keypad, :usage :|}
   {:type :keypad, :usage :||}
   {:type :keypad, :usage :double-colon}
   {:type :keypad, :usage :#}
   {:type :keypad, :usage :space}
   {:type :keypad, :usage :at}
   {:type :keypad, :usage :!}
   {:type :keypad, :usage :memory-store}
   {:type :keypad, :usage :memory-recall}
   {:type :keypad, :usage :memory-clear}
   {:type :keypad, :usage :memory-add}
   {:type :keypad, :usage :memory-substract}
   {:type :keypad, :usage :memory-multiply}
   {:type :keypad, :usage :memory-divide}
   {:type :keypad, :usage :sign-invert}
   {:type :keypad, :usage :clear}
   {:type :keypad, :usage :clear-entry}
   {:type :keypad, :usage :binary}
   {:type :keypad, :usage :octal}
   {:type :keypad, :usage :decimal}
   {:type :keypad, :usage :hexadecimal}
   nil
   nil
   {:type :keyboard, :usage :left-control}
   {:type :keyboard, :usage :left-shift}
   {:type :keyboard, :usage :left-alt}
   {:type :keyboard, :usage :left-gui}
   {:type :keyboard, :usage :right-control}
   {:type :keyboard, :usage :right-shift}
   {:type :keyboard, :usage :right-alt}
   {:type :keyboard, :usage :right-gui}])

(defn- fallback-processor [_ code]
  {:plugin :unknown
   :key-code code})

(defn- control-held [mods flags]
  (if (bit-test flags 0)
    (conj mods :left-control)
    mods))

(defn- left-alt-held [mods flags]
  (if (bit-test flags 1)
    (conj mods :left-alt)
    mods))

(defn- right-alt-held [mods flags]
  (if (bit-test flags 2)
    (conj mods :right-alt)
    mods))

(defn- shift-held [mods flags]
  (if (bit-test flags 3)
    (conj mods :left-shift)
    mods))

(defn- gui-held [mods flags]
  (if (bit-test flags 4)
    (conj mods :left-gui)
    mods))

(defn- hid-processor [key code]
  (let [flags (bit-shift-right code 8)
        key-code (bit-and code 0x00ff)]
    (cond
      ;; Transparent keys
      (= code 0xffff) {:plugin :core
                       :type :transparent}
      ;; Reserved bit set
      (bit-test flags 7) key
      ;; Normal keys (with optional modifiers)
      (and (>= flags 0)
           (<= flags (bit-shift-left 1 4))) (assoc (nth HID-Codes key-code {:type :unknown :code key-code})
                                                   :plugin :core
                                                   :modifiers (reduce #(%2 %1 flags) []
                                                                      [control-held left-alt-held right-alt-held shift-held gui-held]))
      ;; Synthetic
      (bit-test flags 6) key
      :default key)))

(defn- key-cleanup [key]
  (if (= :unknown (:plugin key))
    key
    (dissoc key :key-code)))

(def processors (atom [fallback-processor hid-processor]))

(defn from-code [code]
  (key-cleanup (reduce #(%2 %1 code) {} @processors)))

(defmulti display
  (fn [react-key key]
    [(:plugin key)]))

(defn key-button [react-key color content]
  [:button.btn {:type :button
                :class (str "btn-" (name color))
                :key react-key}
   content])

(defmulti with-modifier
  (fn [mod key-name]
    mod))

(defmethod with-modifier :left-control [_ key-name]
  (str "LCTRL(" key-name ")"))
(defmethod with-modifier :left-alt [_ key-name]
  (str "LALT(" key-name ")"))
(defmethod with-modifier :right-alt [_ key-name]
  (str "RALT(" key-name ")"))
(defmethod with-modifier :left-shift [_ key-name]
  (str "LSHIFT(" key-name ")"))
(defmethod with-modifier :left-gui [_ key-name]
  (str "LGUI(" key-name ")"))

(defn with-modifiers [key-name mods]
  (reduce #(with-modifier %2 %1) key-name mods))

(defmethod display :default [react-key key]
  (key-button react-key :secondary key))

(defmethod display [:core] [react-key key]
  (condp = (:type key)
    :transparent (key-button react-key :secondary "<Transparent>")
    :no-key (key-button react-key :danger "<NoKey>")
    (key-button react-key :secondary (with-modifiers (s/capitalize (name (:usage key)))
                                       (:modifiers key)))))

(defmethod display [:unknown] [react-key key]
  (key-button react-key :warning
              [:em {:title (str "code: " (:key-code key))}
               "<???>"]))
