(ns chrysalis.plugin.Kaleidoscope.OneShot.keymap
  (:require [chrysalis.plugin.page.keymap.events :as events]

            [chrysalis.key :as key]))



;; TODO: wrap in something so it can be activated/deactived on command
(events/add-edit-tab!
  {:title "OneShot Modifiers"
   :modifiers? false
   :keys
   (into []
         (map (fn [{mod :key}]
                {:plugin :oneshot
                 :type :modifier
                 :modifier mod}))
         (events/keys-like #"(left|right)-(control|shift|alt|gui)"))})

(events/add-edit-tab!
  {:title "OneShot Layers"
   :modifiers? false
   :keys (into []
               (map (fn [layer]
                      {:plugin :oneshot
                       :type :layer
                       :layer layer}))
               ;; Docs say 24 layers are supported, by OSM_FIRST - OSM_LAST = 8?
               (range 8))})
