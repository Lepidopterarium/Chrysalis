(ns chrysalis.plugin.page.eeprom.core
  (:require [chrysalis.ui.page :as page]
            [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.plugin.page.eeprom.events :as events]
            [reagent.core :as r]))

(def contents-style
  [
   [:.eeprom-contents
   {:width "100%"
    :font-size "0.8em"
    :font-family "monospace"}
   [:th
    {:text-align "center"
     :font-weight "normal"}]
   [:td.row-idx
    {:text-align "right"}]
   [:td.cell
    {:border "1px solid black"
     :cursor "pointer"}
    [:&.selected
     {:font-weight "bold"}]]]

   [:.selected-cell
    {:position "fixed"
     :top "40%"}]])

(defn n->hex
  ([n] (n->hex n 2))
  ([n width]
   (let [s (.toString n 16)]
     (str "0x" (apply str (repeat (- width (count s)) "0"))
          s))))

(defn <contents>
  [contents]
  ;; TODO: layout with a fixed width using a table instead of flexbox?
  ;; TODO: parse settings header & indicate what represents what
  ;; TODO: ability to select a cell & edit
  [:table.eeprom-contents
   [:thead
    (into
      [:tr
       [:th]]
      (map (fn [n] [:th (n->hex n)]) (range 16)))]
   [:tbody
    (doall
      (for [[r-idx row] (map-indexed vector (partition 16 contents))]
        ^{:key r-idx}
        [:tr
         [:td.row-idx (n->hex (* 16 r-idx))]
         (doall
           (for [[idx c] (map-indexed vector row)]
             ^{:key idx}
             [:td.cell
              {:on-click (fn [_] (events/set-selected! (+ idx (* r-idx 16))))
               :class (when (= (+ idx (* r-idx 16)) (events/selected-index))
                        "selected")}
              (n->hex c)]))]))]])

(defn cell-editor
  [cell-idx]
  (let [new-val (r/atom 0)
        input-style (r/atom :hex)]
    (fn [cell-idx]
      [:div.selected-cell
       [:p "Cell " (n->hex (events/selected-index))]
       [:pre (n->hex (events/content-cell cell-idx))]
       [:label "Input method:"
        [:select {:on-change (fn [e] (reset! input-style (keyword (.. e -target -value))))}
         [:option {:value "hex"} "Hex"]
         [:option {:value "decimal"} "Decimal"]]]
       (if (= @input-style :hex)
         [:label "0x"
          [:input {:type :text
                   :size 2
                   :value (.toString @new-val 16)
                   :on-change
                   (fn [e]
                     (let [n (js/parseInt (.. e -target -value) 16)]
                       (when (and (not (js/isNaN n))
                                  (<= 0 n 255))
                         (reset! new-val n))))}]]
         [:input {:type :number
                  :min 0
                  :max 255
                  :value @new-val
                  :on-change
                  (fn [e]
                    (let [n (js/parseInt (.. e -target -value) 10)]
                      (when (and (not (js/isNaN n))
                                 (<= 0 n 255))
                        (reset! new-val n))))}])
       [:button {:on-click (fn [_]
                             (events/set-cell! cell-idx @new-val))}
        (str "Set cell to " (n->hex @new-val))]

       [:br]
       [:br]
       [:br]
       [:br]
       [:br]
       [:button
        {:on-click (fn [_] (events/commit-eeprom-changes!))}
         "Commit changes"]])))

(defn render
  []
  [:div.container-fluid {:id :eeprom}
   [ui/style [:#page
              [:#eeprom
               contents-style]]]
   [:div.row
    [:div.col-sm-12.text-center
     [:p "Free space for settings: " (events/free-space) " bytes"]]]
   [:div.row
    [:div.col-sm-10.text-center
     [:p "Contents: (" (count (events/contents)) " bytes)"]
     [<contents> (events/contents)]]
    [:div.col-sm-2.text-center
     (when-let [idx (events/selected-index)]
       [cell-editor idx])]]])

(page/add! :eeprom {:name "EEPROM Contents"
                    :index 11
                    :disable? (fn [] (nil? (device/current)))
                    :device/need? true
                    :render render
                    :events {:eeprom/contents :update
                             :eeprom/space :update}})
