(ns electron.core)

(def electron       (js/require "electron"))
(def app            (.-app electron))
(def browser-window (.-BrowserWindow electron))
(def crash-reporter (.-crashReporter electron))
(def shell          (.-shell electron))
(def context-menu   (js/require "electron-context-menu"))

(def main-window (atom nil))

(defn init-browser []
  (reset! main-window (browser-window.
                        (clj->js {:width 1200
                                  :height 600
                                  :icon "images/kaleidoscope-logo-ph.png"
                                  :autoHideMenuBar true})))
  (.loadURL @main-window (str "file://" js/__dirname "/index.html"))
  (.on (.-webContents @main-window)
       "will-navigate" (fn [e url]
                         (.preventDefault e)
                         (.openExternal shell url)))
  (.on @main-window "closed" #(reset! main-window nil)))


(.on app "window-all-closed" #(when-not (= js/process.platform "darwin")
                                (.quit app)))
(.on app "ready" init-browser)

(context-menu #js {"showInspectElement" true})
