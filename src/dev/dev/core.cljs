(ns dev.core
  (:require [figwheel.client :as fw :include-macros true]
            [chrysalis.app :as chrysalis]))

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback (fn []
                    (print "reloaded")
                    (chrysalis/reload!)))
