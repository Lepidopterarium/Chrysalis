(ns dev.core
  (:require [figwheel.client :as fw :include-macros true]
            [re-frisk.core :refer [enable-re-frisk!]]
            [chrysalis.app :as chrysalis]))

(enable-console-print!)
(enable-re-frisk! {:width "600px"
                   :height "400px"})

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback (fn []
                    (print "reloaded")
                    (chrysalis/reload!)))
