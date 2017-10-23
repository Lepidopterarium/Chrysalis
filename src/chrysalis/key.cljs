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
            [clojure.string :as s]

            [chrysalis.command.post-process :as post-process]
            [chrysalis.device :as device]))

(def HID-Codes
  [{:plugin :core, :key nil}
   nil
   nil
   nil
   {:plugin :core, :key :a}
   {:plugin :core, :key :b}
   {:plugin :core, :key :c}
   {:plugin :core, :key :d}
   {:plugin :core, :key :e}
   {:plugin :core, :key :f}
   {:plugin :core, :key :g}
   {:plugin :core, :key :h}
   {:plugin :core, :key :i}
   {:plugin :core, :key :j}
   {:plugin :core, :key :k}
   {:plugin :core, :key :l}
   {:plugin :core, :key :m}
   {:plugin :core, :key :n}
   {:plugin :core, :key :o}
   {:plugin :core, :key :p}
   {:plugin :core, :key :q}
   {:plugin :core, :key :r}
   {:plugin :core, :key :s}
   {:plugin :core, :key :t}
   {:plugin :core, :key :u}
   {:plugin :core, :key :v}
   {:plugin :core, :key :w}
   {:plugin :core, :key :x}
   {:plugin :core, :key :y}
   {:plugin :core, :key :z}
   {:plugin :core, :key :1}
   {:plugin :core, :key :2}
   {:plugin :core, :key :3}
   {:plugin :core, :key :4}
   {:plugin :core, :key :5}
   {:plugin :core, :key :6}
   {:plugin :core, :key :7}
   {:plugin :core, :key :8}
   {:plugin :core, :key :9}
   {:plugin :core, :key :0}
   {:plugin :core, :key :enter}
   {:plugin :core, :key :escape}
   {:plugin :core, :key :backspace}
   {:plugin :core, :key :tab}
   {:plugin :core, :key :space}
   {:plugin :core, :key :minus}
   {:plugin :core, :key :equals}
   {:plugin :core, :key :left-square-bracket}
   {:plugin :core, :key :right-square-bracket}
   {:plugin :core, :key :backslash}
   {:plugin :core, :key :non-US-pound}
   {:plugin :core, :key :semicolon}
   {:plugin :core, :key :quote}
   {:plugin :core, :key :backtick}
   {:plugin :core, :key :comma}
   {:plugin :core, :key :dot}
   {:plugin :core, :key :slash}
   {:plugin :core, :key :caps-lock}
   {:plugin :core, :key :F1}
   {:plugin :core, :key :F2}
   {:plugin :core, :key :F3}
   {:plugin :core, :key :F4}
   {:plugin :core, :key :F5}
   {:plugin :core, :key :F6}
   {:plugin :core, :key :F7}
   {:plugin :core, :key :F8}
   {:plugin :core, :key :F9}
   {:plugin :core, :key :F10}
   {:plugin :core, :key :F11}
   {:plugin :core, :key :F12}
   {:plugin :core, :key :print-screen}
   {:plugin :core, :key :scroll-lock}
   {:plugin :core, :key :pause}
   {:plugin :core, :key :insert}
   {:plugin :core, :key :home}
   {:plugin :core, :key :page-up}
   {:plugin :core, :key :delete}
   {:plugin :core, :key :end}
   {:plugin :core, :key :page-down}
   {:plugin :core, :key :right-arrow}
   {:plugin :core, :key :left-arrow}
   {:plugin :core, :key :down-arrow}
   {:plugin :core, :key :up-arrow}
   {:plugin :core, :key :num-lock}
   {:plugin :core, :key :keypad_divide}
   {:plugin :core, :key :keypad_multiply}
   {:plugin :core, :key :keypad_minus}
   {:plugin :core, :key :keypad_plus}
   {:plugin :core, :key :keypad_enter}
   {:plugin :core, :key :keypad_1}
   {:plugin :core, :key :keypad_2}
   {:plugin :core, :key :keypad_3}
   {:plugin :core, :key :keypad_4}
   {:plugin :core, :key :keypad_5}
   {:plugin :core, :key :keypad_6}
   {:plugin :core, :key :keypad_7}
   {:plugin :core, :key :keypad_8}
   {:plugin :core, :key :keypad_9}
   {:plugin :core, :key :keypad_0}
   {:plugin :core, :key :keypad_dot}
   {:plugin :core, :key :non-US-slash}
   {:plugin :core, :key :application}
   {:plugin :core, :key :power}
   {:plugin :core, :key :keypad_equals}
   {:plugin :core, :key :F13}
   {:plugin :core, :key :F14}
   {:plugin :core, :key :F15}
   {:plugin :core, :key :F16}
   {:plugin :core, :key :F17}
   {:plugin :core, :key :F18}
   {:plugin :core, :key :F19}
   {:plugin :core, :key :F20}
   {:plugin :core, :key :F21}
   {:plugin :core, :key :F22}
   {:plugin :core, :key :F23}
   {:plugin :core, :key :F24}
   {:plugin :core, :key :execute}
   {:plugin :core, :key :help}
   {:plugin :core, :key :menu}
   {:plugin :core, :key :select}
   {:plugin :core, :key :stop}
   {:plugin :core, :key :again}
   {:plugin :core, :key :undo}
   {:plugin :core, :key :cut}
   {:plugin :core, :key :copy}
   {:plugin :core, :key :paste}
   {:plugin :core, :key :find}
   {:plugin :core, :key :mute}
   {:plugin :core, :key :volume-up}
   {:plugin :core, :key :volume-down}
   {:plugin :core, :key :locking-caps-lock}
   {:plugin :core, :key :locking-num-lock}
   {:plugin :core, :key :locking-scroll-lock}
   {:plugin :core, :key :keypad_comma}
   {:plugin :core, :key :keypad_equal-sign}
   {:plugin :core, :key :international-1}
   {:plugin :core, :key :international-2}
   {:plugin :core, :key :international-3}
   {:plugin :core, :key :international-4}
   {:plugin :core, :key :international-5}
   {:plugin :core, :key :international-6}
   {:plugin :core, :key :international-7}
   {:plugin :core, :key :international-8}
   {:plugin :core, :key :international-9}
   {:plugin :core, :key :lang-1}
   {:plugin :core, :key :lang-2}
   {:plugin :core, :key :lang-3}
   {:plugin :core, :key :lang-4}
   {:plugin :core, :key :lang-5}
   {:plugin :core, :key :lang-6}
   {:plugin :core, :key :lang-7}
   {:plugin :core, :key :lang-8}
   {:plugin :core, :key :lang-9}
   {:plugin :core, :key :alternate-erase}
   {:plugin :core, :key :sysrq}
   {:plugin :core, :key :cancel}
   {:plugin :core, :key :clear}
   {:plugin :core, :key :prior}
   {:plugin :core, :key :return}
   {:plugin :core, :key :separator}
   {:plugin :core, :key :out}
   {:plugin :core, :key :oper}
   {:plugin :core, :key :clear-or-again}
   {:plugin :core, :key :crsel-or-props}
   {:plugin :core, :key :exsel}
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
   {:plugin :core, :key :keypad_00}
   {:plugin :core, :key :keypad_000}
   {:plugin :core, :key :keypad_thousands-separator}
   {:plugin :core, :key :keypad_decimal-separator}
   {:plugin :core, :key :keypad_currency-unit}
   {:plugin :core, :key :keypad_currency-sub-unit}
   {:plugin :core, :key :keypad_opening-parens}
   {:plugin :core, :key :keypad_closing-parens}
   {:plugin :core, :key :keypad_opening-curly-braces}
   {:plugin :core, :key :keypad_closing-curly-brackes}
   {:plugin :core, :key :keypad_tab}
   {:plugin :core, :key :keypad_backspace}
   {:plugin :core, :key :keypad_a}
   {:plugin :core, :key :keypad_b}
   {:plugin :core, :key :keypad_c}
   {:plugin :core, :key :keypad_d}
   {:plugin :core, :key :keypad_e}
   {:plugin :core, :key :keypad_f}
   {:plugin :core, :key :keypad_xor}
   {:plugin :core, :key :keypad_caret}
   {:plugin :core, :key :keypad_percent}
   {:plugin :core, :key :keypad_<}
   {:plugin :core, :key :keypad_>}
   {:plugin :core, :key :keypad_&}
   {:plugin :core, :key :keypad_&&}
   {:plugin :core, :key :keypad_|}
   {:plugin :core, :key :keypad_||}
   {:plugin :core, :key :keypad_colon}
   {:plugin :core, :key :keypad_#}
   {:plugin :core, :key :keypad_space}
   {:plugin :core, :key :keypad_at}
   {:plugin :core, :key :keypad_!}
   {:plugin :core, :key :keypad_memory-store}
   {:plugin :core, :key :keypad_memory-recall}
   {:plugin :core, :key :keypad_memory-clear}
   {:plugin :core, :key :keypad_memory-add}
   {:plugin :core, :key :keypad_memory-substract}
   {:plugin :core, :key :keypad_memory-multiply}
   {:plugin :core, :key :keypad_memory-divide}
   {:plugin :core, :key :keypad_sign-invert}
   {:plugin :core, :key :keypad_clear}
   {:plugin :core, :key :keypad_clear-entry}
   {:plugin :core, :key :keypad_binary}
   {:plugin :core, :key :keypad_octal}
   {:plugin :core, :key :keypad_decimal}
   {:plugin :core, :key :keypad_hexadecimal}
   nil
   nil
   {:plugin :core, :key :left-control}
   {:plugin :core, :key :left-shift}
   {:plugin :core, :key :left-alt}
   {:plugin :core, :key :left-gui}
   {:plugin :core, :key :right-control}
   {:plugin :core, :key :right-shift}
   {:plugin :core, :key :right-alt}
   {:plugin :core, :key :right-gui}])

(def key->hid
  "Given a key name, like :right-arrow, get the corresponding numeric
  HID code"
  (-> (into {} (map-indexed (fn [i {k :key}] [k i])) HID-Codes)
      (assoc nil 0)))

(defn- fallback-processor [_ code]
  {:plugin :unknown
   :key-code code})

(def modifier-bits
  {:control 0
   :left-alt 1
   :right-alt 2
   :shift 3
   :gui 4})

(defn- modifiers-set
  "Given the flags bits, return a set of keywords indicating which
  modifiers have been set."
  [flags]
  (reduce (fn [mods [mod bit]]
            (if (bit-test flags bit)
              (conj mods mod)
              mods))
          #{}
          modifier-bits))

(defn- hid-processor [key code]
  (let [flags (bit-shift-right code 8)
        key-code (bit-and code 0x00ff)]
    (cond
      ;; Transparent keys
      (= code 0xffff) {:plugin :core
                       :key :transparent}
      ;; Reserved bit set
      (bit-test flags 7) key
      ;; Normal keys (with optional modifiers)
      (and (>= flags 0)
           (<= flags (bit-shift-left 1 4)))
      (assoc (nth HID-Codes key-code {:plugin :unknown :code key-code})
             :plugin :core
             :modifiers (modifiers-set flags))
      ;; Synthetic
      (bit-test flags 6) key
      :default (keyword key))))

(defn- key-cleanup [key]
  (if (= :unknown (:plugin key))
    key
    (dissoc key :key-code)))

(def processors (atom [fallback-processor hid-processor]))

(defn from-code [code]
  (key-cleanup (reduce #(%2 %1 code) {} @processors)))

(defmulti format
  (fn [key]
    [(:plugin key)]))

(defmethod format :default [key]
  {:primary-text "<???>"})

(def key->description
  {nil {:primary-text "NoKey"
        :foreground "red"}
   :transparent {:primary-text "Trns"
                 :foreground "red"}
   :left-shift {:primary-text "Shift L"}
   :right-shift {:primary-text "Shift R"}
   :left-alt {:primary-text "Alt L"}
   :right-alt {:primary-text "Alt R"}
   :left-control {:primary-text "Ctrl L"}
   :right-control {:primary-text "Ctrl R"}
   :space {:primary-text "SPC"}
   :backspace {:primary-text "BSPC"}
   :escape {:primary-text "ESC"}
   :minus {:primary-text "-"}
   :equals {:primary-text "="}
   :left-square-bracket {:primary-text "["}
   :right-square-bracket {:primary-text "]"}
   :backslash {:primary-text "\\"}
   :non-US-pound {:primary-text "£"}
   :semicolon {:primary-text ";"}
   :quote {:primary-text "\""}
   :backtick {:primary-text "`"}
   :comma {:primary-text ","}
   :dot {:primary-text "."}
   :slash {:primary-text "/"}
   :caps-lock {:primary-text "Caps"}
   :print-screen {:primary-text "Pr Sc"}
   :scroll-lock {:primary-text "⤓"}
   :insert {:primary-text "Ins"}
   :page-up {:primary-text "PgUp"}
   :delete {:primary-text "Del"}
   :page-down {:primary-text "PgDn"}
   :right-arrow {:primary-text "→"}
   :left-arrow {:primary-text "←"}
   :down-arrow {:primary-text "↓"}
   :up-arrow {:primary-text "↑"}
   :num-lock {:primary-text "NumLck"}
   :keypad_divide {:primary-text "/"}
   :keypad_multiply {:primary-text "*"}
   :keypad_minus {:primary-text "-"}
   :keypad_plus {:primary-text "+"}
   :keypad_enter {:primary-text "Enter"}
   :keypad_1 {:primary-text "1"}
   :keypad_2 {:primary-text "2"}
   :keypad_3 {:primary-text "3"}
   :keypad_4 {:primary-text "4"}
   :keypad_5 {:primary-text "5"}
   :keypad_6 {:primary-text "6"}
   :keypad_7 {:primary-text "7"}
   :keypad_8 {:primary-text "8"}
   :keypad_9 {:primary-text "9"}
   :keypad_0 {:primary-text "0"}
   :keypad_dot {:primary-text "."}
   :keypad_equals {:primary-text "="}
   :execute {:primary-text ""}
   :select {:primary-text "Sel"}
   :volume-up {:primary-text "Vol +"}
   :volume-down {:primary-text "Vol -"}
   :keypad_comma {:primary-text ","}
   :keypad_equal-sign {:primary-text "="}
   :return {:primary-text "Ret"}
   :keypad_opening-parens {:primary-text "("}
   :keypad_closing-parens {:primary-text ")"}
   :keypad_opening-curly-braces {:primary-text "{"}
   :keypad_closing-curly-brackes {:primary-text "}"}
   :keypad_tab {:primary-text "Tab"}
   :keypad_backspace {:primary-text "BSPC"}
   :keypad_a {:primary-text "a"}
   :keypad_b {:primary-text "b"}
   :keypad_c {:primary-text "c"}
   :keypad_d {:primary-text "d"}
   :keypad_e {:primary-text "e"}
   :keypad_f {:primary-text "f"}
   :keypad_caret {:primary-text "^"}
   :keypad_percent {:primary-text "%"}
   :keypad_< {:primary-text "<"}
   :keypad_> {:primary-text ">"}
   :keypad_& {:primary-text "&"}
   :keypad_| {:primary-text "|"}
   :keypad_colon {:primary-text ":"}
   :keypad_# {:primary-text "#"}
   :keypad_space {:primary-text "SPC"}
   :keypad_at {:primary-text "@"}
   :keypad_! {:primary-text "!"}})

(def mod->short-name
  {:control "C"
   :left-alt "M"
   :gui "s"
   :shift "S"
   :right-alt "AltGr"})

(defmethod format [:core] [{key :key mods :modifiers}]
  (assoc (key->description key {:primary-text (s/capitalize ((fnil name "???") key))})
         :secondary-text (s/join "-" (map mod->short-name mods))))

(defmethod post-process/format [:keymap.map] [_ text]
  (let [keymap-size (apply * (get-in (device/current) [:meta :matrix]))]
    (->> (.split text " ")
        (map int)
        (map from-code)
        (partition keymap-size)
        (map vec)
        vec)))

(defmulti unformat :plugin)

(defn mods->flags
  [mods]
  (reduce (fn [flags mod]
            (bit-set flags (modifier-bits mod)))
          0
          mods))

(defmethod unformat :core
  [key]
  (if (= (:key key) :transparent)
    0xffff
    (let [key-code (key->hid (:key key))
          modifiers (mods->flags (:modifiers key))]
      (bit-or (bit-shift-left modifiers 8) key-code))))

(defmethod unformat :unknown
  [key]
  (:keycode key))
