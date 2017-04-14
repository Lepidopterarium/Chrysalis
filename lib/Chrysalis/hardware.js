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

function load (moduleName) {
    require("../Chrysalis-Hardware-" + moduleName + "/index")
}

function loadAll () {
    load ("Model01")

    Chrysalis.on ("device-scan", (port) => {
        Chrysalis.hardware.devices.forEach ((device) => {
            if (device.meta.manufacturer == port.manufacturer)
                Chrysalis.emit ("device-detected", port)
        })
    })
}

function select (version) {
    selected = null
    Chrysalis.hardware.devices.forEach ((device) => {
        if (device.meta.manufacturer == version.device.manufacturer &&
            device.meta.product == version.device.product)
            selected = device
    })
    return selected
}

module.exports = {
    "load": load,
    "loadAll": loadAll,
    "select": select,
    "devices": [],
}
