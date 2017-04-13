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

function cleanResult (res) {
    return res.substring(0, res.length - 4)
}

function sendCommand(port, command, callBack) {
    port.write (command + "\n", function () {
        port.drain (function (e) {
            window.setTimeout (function () {
                callBack (cleanResult (port.read().toString()))
            }, 100)
        })
    })
}

exports.sendCommand = sendCommand
