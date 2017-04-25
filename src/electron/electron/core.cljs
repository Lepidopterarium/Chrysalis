(ns electron.core)

(def electron       (js/require "electron"))
(def app            (.-app electron))
(def browser-window (.-BrowserWindow electron))
(def crash-reporter (.-crashReporter electron))

(def main-window (atom nil))

(defn init-browser []
  (reset! main-window (browser-window.
                        (clj->js {:width 1200
                                  :height 600
                                  :autoHideMenuBar true})))
  (.loadURL @main-window (str "file://" js/__dirname "/index.html"))
  (.on @main-window "closed" #(reset! main-window nil)))


(comment
  (.start crash-reporter
         (clj->js
          {:companyName "The MadHouse Project"
           :productName "Chrysalis"
           :submitURL "https://example.com/submit-url"
           :autoSubmit false})))

(.on app "window-all-closed" #(when-not (= js/process.platform "darwin")
                                (.quit app)))
(.on app "ready" init-browser)
