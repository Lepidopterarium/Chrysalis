<!-- -*- mode: markdown; fill-column: 8192 -*- -->

# Chrysalis 0.0.3
Released on 2017-06-04

## Notable changes

* The `Shortcut` is now far better supported: it has an icon, and a layout image, and the LED theme editor is usable with it too (well, somewhat, anyway).
* Chrysalis gained a few key bindings:
  - The various pages can be accessed with `Alt+N`.
  - On the device selector page, the devices can be selected with `Ctrl+N`.
* Comes with ClojureScript DevTools pre-installed, for ease of development.
* The whole application has been re-based on top of [re-frame][re-frame], to provide a more reliable, proven foundation. The net result is fewer bugs, and a more responsive interface.

 [re-frame]: https://github.com/Day8/re-frame

## LED Theme Editor

* The color picker has been redone, and we are using the [react-color][react-color] library. It doesn't only look better, it is more usable too.
* The editor can now "live update", meaning that whenever any key color is changed, it will apply the theme automatically, without having to press the `Apply` button. This is turned off by default, however.

 [react-color]: http://casesandberg.github.io/react-color/

## REPL, Wire Traffic Spy

* The REPL and Wire Traffic history is now limited to 50 items, to put a limit on resource use, and scrolling.
* Minor polishing was done on the look of the history item boxes:
  - The device used for the command are now shown in the `Wire Traffic Spy` view, too.
  - The device used is placed further down from the command response.

## Known issues

* Settings are not properly saved at the moment.

# Chrysalis 0.0.2
Released on 2017-05-12

## Notable changes

* Instead of scanning devices only on startup, or by a manual trigger, Chrysalis now re-scans the devices whenever an USB device is plugged in, or out.
* The menu has been redesigned, and the various pages now appear in a drop-down. ([#71](https://github.com/algernon/Chrysalis/issues/71))
* We now have an `About` page. ([#62](https://github.com/algernon/Chrysalis/issues/62))
* Settings are now stored and loaded when needed. This includes the window state, too.
  
## LED Theme Editor ([#54](https://github.com/algernon/Chrysalis/issues/54))

* New page, to edit LED themes. An unpolished, proof of concept for now.
* Presets are saved to disk, and loaded when needed. Presets are tied to a particular product.

## REPL

* The history item is displayed even while the results are still coming in - with a spinner in place of the output.
  
## Firmware Flasher

* The name of the last `HEX` file flashed is saved on disk, per-product.
  
## Wire Traffic Spy ([#64](https://github.com/algernon/Chrysalis/issues/64))

* New page, to see what we send to the keyboard, and what we get back, without any transformations, raw as it is.

## Behind the scenes

* Building from source was made considerably easier.
* Communicating with the keyboard was made a lot more responsive, by transitioning from fixed timeouts to event-based I/O. ([#58](https://github.com/algernon/Chrysalis/issues/58), [#65](https://github.com/algernon/Chrysalis/issues/65))
* Right-click now brings up a simple context menu, primarily for the `Inspect item` functionality during development.
* Chrysalis now sets an icon for its application (a placeholder at this time).

# Chrysalis 0.0.1
Released on 2017-04-30

Initial public alpha release.
