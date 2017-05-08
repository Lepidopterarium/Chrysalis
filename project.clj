(defproject Chrysalis "0.1.0-SNAPSHOT"
  :source-paths ["src"]
  :description "Kaleidoscope Command Center"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [figwheel "0.5.8"]
                 [figwheel-sidecar "0.5.8"]
                 [reagent "0.6.0"]
                 [ring/ring-core "1.5.0"]
                 [org.clojure/core.async "0.3.442"]
                 [com.cemerick/piggieback "0.2.1"]
                 [hickory "0.7.1"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :npm {:dependencies [[avrgirl-arduino "^2.0.0"]
                       [bootstrap "^4.0.0-alpha.4"]
                       [jquery "^3.1.0"]
                       [font-awesome "^4.7.0"]
                       [tether "^1.3.7"]
                       [serialport "git+https://github.com/EmergingTechnologyAdvisors/node-serialport.git#master"]
                       [usb "^1.2.0"]
                       [electron-context-menu "^0.9.0"]]
        :devDependencies [[electron "^1.6.8-beta"]
                          [electron-rebuild "^1.5.7"]
                          [foreman "^2.0.0"]]
        :root "resources/public"
        :package {:scripts {:start-ui "electron .."
                            :build "electron-rebuild -w usb,serialport && lein cljsbuild once electron-dev && lein cljsbuild once frontend-dev"
                            :start "nf -j ../../Procfile start"}}}
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.8"]
            [lein-resource "16.9.1"]
            [lein-npm "0.6.2"]]
  :hooks [leiningen.resource]

  :clean-targets ^{:protect false} ["resources/public/main.js"
                                    "resources/public/js/ui-core.js"
                                    "resources/public/js/ui-core.js.map"
                                    "resources/public/js/ui-out"]
  :cljsbuild
  {:builds
   [{:source-paths ["src/electron"]
     :id "electron-dev"
     :compiler {:output-to "resources/public/main.js"
                :output-dir "resources/public/js/electron-dev"
                :optimizations :simple
                :pretty-print true
                :cache-analysis true}}
    {:source-paths ["src/chrysalis" "src/dev"]
     :id "frontend-dev"
     :compiler {:output-to "resources/public/js/ui-core.js"
                :output-dir "resources/public/js/ui-out"
                :source-map true
                :asset-path "js/ui-out"
                :optimizations :none
                :cache-analysis true
                :main "dev.core"}}
    {:source-paths ["src/electron"]
     :id "electron-release"
     :compiler {:output-to "resources/public/main.js"
                :output-dir "resources/public/js/electron-release"
                :optimizations :simple
                :pretty-print true
                :cache-analysis true}}
    {:source-paths ["src/chrysalis"]
     :id "frontend-release"
     :compiler {:output-to "resources/public/js/ui-core.js"
                :output-dir "resources/public/js/ui-release-out"
                :source-map "resources/public/js/ui-core.js.map"
                :optimizations :simple
                :cache-analysis true
                :main "chrysalis.app"}}]}
  :figwheel {:http-server-root "public"
             :css-dirs ["resources/public/css"]
             :ring-handler tools.figwheel-middleware/app
             :server-port 3449}
  :resource {:resource-paths ["src/chrysalis/plugin/hardware/model01"
                              "src/chrysalis/plugin/hardware/virtual"
                              ["src/chrysalis/plugin/page/repl" {:target-path "resources/public/css/plugins/page"}]
                              ["src/chrysalis/plugin/page/devices" {:target-path "resources/public/css/plugins/devices"}]
                              ["src/chrysalis/plugin/page/firmware" {:target-path "resources/public/css/plugins/firmware"}]]
             :includes [#".*\.png" #".*\.css" #".*\.svg"]
             :target-path "resources/public/images/plugins"
             :skip-stencil [#".*"]}

  :aliases {"build" ["do" ["npm" "run" "build"] "resource"]
            "start-ui" ["do" ["npm" "run" "start-ui"]]})
