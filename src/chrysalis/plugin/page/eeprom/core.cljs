(ns chrysalis.plugin.page.eeprom.core
  (:require [chrysalis.ui.page :as page]
            [chrysalis.device :as device]
            [chrysalis.ui :as ui]
            [chrysalis.plugin.page.eeprom.events :as events]))

(def contents-style
  [:.eeprom-contents
   {:display "flex"
    :flex-wrap "wrap"}
   [:>.cell
    {:margin "0.5em"
     :background-color "cyan"
     :font-family "monospace"}]])

(defn n->hex
  ([n] (n->hex n 2))
  ([n width]
   (let [s (.toString n 16)]
     (str "0x" (apply str (repeat (- width (count s)) "0"))
          s))))

(defn <contents>
  [contents]
  [:div.eeprom-contents
   (doall
     (for [[idx c] (map-indexed vector contents)]
       ^{:key idx}
       [:span.cell (n->hex c)]))])

(defn render
  []
  [:div.container-fluid {:id :eeprom}
   [ui/style [:#page
              [:#eeprom
               contents-style]]]
   [:div.row
    [:div.col-sm-9.text-center
     [:p "EEPROM stuff"]
     [:p "Free space for settings: " (events/free-space) " bytes"]
     [:p "Contents: (" (count (events/contents)) " bytes)"]
     [<contents> (events/contents)]]]])

(page/add! :eeprom {:name "EEPROM Contents"
                    :index 11
                    :disable? (fn [] (nil? (device/current)))
                    :device/need? true
                    :render render
                    :events {:eeprom/contents :update
                             :eeprom/space :update}})
