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

function version() {
    return new Promise ((resolve, reject) => {
        Chrysalis.utils.sendCommand ("version", function (result) {
            var [version_and_device, date] = result.split (/ \| /)
            var t = version_and_device.split(/ /)
            var fwver = t[0].split(/\//)[1]
            var [manufacturer, product] = t.slice(1).join (" ").split (/\//)
            resolve ({"device": {"manufacturer": manufacturer, "product": product}, "firmware": fwver, "timestamp": date})
        })
    });
}

function send(command) {
    return new Promise ((resolve, reject) => {
        Chrysalis.utils.sendCommand (command, function (result) {
            resolve (result)
        })
    })
}

exports.send = send
exports.version = version
