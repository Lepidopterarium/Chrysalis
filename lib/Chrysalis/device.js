/* Chrysalis -- Kaleidoscope Command Center
 * Copyright (C) 2017  Gergely Nagy <algernon@madhouse-project.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

const serialport = require('serialport')

function open (devicePath) {
    Chrysalis._port = new serialport (devicePath)

    Chrysalis._port.once('open', function() {
        Chrysalis._port.flush()
        Chrysalis._port.drain()
        Chrysalis._port.read() // dummy read, no idea why it is needed...

        Chrysalis.emit ("device-ready")
    });
}

function detect () {
    return new Promise ((resolve, reject) => {
        var devices = []
        serialport.list(function (err, ports) {
            ports.forEach(function(port) {
                if ((port.manufacturer == "Keyboardio") ||
                    (port.manufacturer == "Shortcut")) {
                    devices.push (port.comName)
                }
            });
            resolve (devices)
            Chrysalis.emit ("device-detected", devices)
        });
    })
}

module.exports = {
    "open": open,
    "detect": detect,
}
