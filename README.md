<!-- -*- mode: markdown; fill-column: 8192 -*- -->

# Chrysalis

[![Latest release](https://img.shields.io/github/release/algernon/Chrysalis/all.svg?style=flat-square)](https://github.com/algernon/Chrysalis/releases/latest)
[![Patreon](https://img.shields.io/badge/Patreon-algernon-red.svg?style=flat-square&colorA=FF5900&colorB=555555)](https://www.patreon.com/algernon)

[Kaleidoscope][kaleidoscope] command center, a [heavy work in progress][chrysalis:project:1.0]. For the latest news, please check the [NEWS.md](NEWS.md) file, or [algernon's blog][blog:algernon:chrysalis.]

 [kaleidoscope]: https://github.com/keyboardio/Kaleidoscope
 [chrysalis:project:1.0]: https://github.com/algernon/Chrysalis/projects/1
 [blog:algernon:chrysalis]: https://asylum.madhouse-project.org/blog/tags/chrysalis/
 
![Chrysalis](docs/screenshots/led-theme-editor.png)

## Quick start

First of all, install [Leiningen](https://leiningen.org/) and [Node](https://nodejs.org/en/), then follow these steps:

* **Install dependencies**: `lein deps`
* **Compile & prepare assets**: `lein build`
* **Start the user-interface**: `lein start-ui`

To start Chrysalis in development mode, with live-reload and everything, use `lein start` instead of the last two steps.

## License

The code is released under the terms of the GNU GPL, version 3 or later. See the [COPYING](COPYING) file for details.
