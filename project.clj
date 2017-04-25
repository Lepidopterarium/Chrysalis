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
                 [com.cemerick/piggieback "0.2.1"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :npm {:dependencies [[avrgirl-arduino "^2.0.0"]
                       [bootstrap "^4.0.0-alpha.4"]
                       [jquery "^3.1.0"]
                       [font-awesome "^4.7.0"]
                       [tether "^1.3.7"]
                       [serialport "git+https://github.com/EmergingTechnologyAdvisors/node-serialport.git#master"]]
        :root "resources/public"}
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.8"]
            [lein-npm "0.6.2"]]

  :clean-targets ^{:protect false} ["resources/main.js"
                                    "resources/public/js/ui-core.js"
                                    "resources/public/js/ui-core.js.map"
                                    "resources/public/js/ui-out"]
  :cljsbuild
  {:builds
   [{:source-paths ["src/electron"]
     :id "electron-dev"
     :compiler {:output-to "resources/main.js"
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
     :compiler {:output-to "resources/main.js"
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
             :server-port 3449})
