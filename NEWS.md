# Chrysalis 0.0.2
Released on 2017-05-12

## Notable changes

* Instead of scanning devices only on startup, or by a manual trigger, Chrysalis
  now re-scans the devices whenever an USB device is plugged in, or out.
* The menu has been redesigned, and the various pages now appear in a drop-down.
  ([#71](https://github.com/algernon/Chrysalis/issues/71))
* We now have an `About` page. ([#62](https://github.com/algernon/Chrysalis/issues/62))
* Settings are now stored and loaded when needed. This includes the window
  state, too.
  
## LED Theme Editor ([#54](https://github.com/algernon/Chrysalis/issues/54))

* New page, to edit LED themes. An unpolished, proof of concept for now.
* Presets are saved to disk, and loaded when needed. Presets are tied to a
  particular product.

## REPL

* The history item is displayed even while the results are still coming in -
  with a spinner in place of the output.
  
## Firmware Flasher

* The name of the last `HEX` file flashed is saved on disk, per-product.
  
## Wire Traffic Spy ([#64](https://github.com/algernon/Chrysalis/issues/64))

* New page, to see what we send to the keyboard, and what we get back, without
  any transformations, raw as it is.

## Behind the scenes

* Building from source was made considerably easier.
* Communicating with the keyboard was made a lot more responsive, by
  transitioning from fixed timeouts to event-based I/O.
  ([#58](https://github.com/algernon/Chrysalis/issues/58),
  [#65](https://github.com/algernon/Chrysalis/issues/65))
* Right-click now brings up a simple context menu, primarily for the `Inspect
  item` functionality during development.
* Chrysalis now sets an icon for its application (a placeholder at this time).

# Chrysalis 0.0.1
Released on 2017-04-30

Initial public alpha release.
