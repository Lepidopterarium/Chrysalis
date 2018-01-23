(ns chrysalis.plugin.page.eeprom.core
  (:require [chrysalis.ui.page :as page]
            [chrysalis.device :as device]
            [chrysalis.ui :as ui]))

(defn render
  []
  [:div.container-fluid {:id :eeprom}
   [ui/style [:#page
              [:#eeprom
               ]]]
   [:div.row
    [:div.col-sm-9.text-center
     [:p "EEPROM stuff"]]]])

(page/add! :eeprom {:name "EEPROM Contents"
                    :index 11
                    :disable? (fn [] (nil? (device/current)))
                    :device/need? true
                    :render render
                    :events {:eeprom/contents :update
                             :eeprom/space :update}})
