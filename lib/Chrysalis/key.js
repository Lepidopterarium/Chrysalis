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

const RESERVED = 0b1000000000000000
const CTRL_HELD = 0b00000001
const LALT_HELD = 0b00000010
const RALT_HELD = 0b00000100
const SHIFT_HELD = 0b00001000
const GUI_HELD = 0b00010000
const SYNTHETIC = 0b01000000

const HIDCodes = [
    "NoKey",
    "Error:RollOver",
    "Error:PostFail",
    "Error:Undefined",
    "A",
    "B",
    "C",
    "D",
    "E",
    "F",
    "G",
    "H",
    "I",
    "J",
    "K",
    "L",
    "M",
    "N",
    "O",
    "P",
    "Q",
    "R",
    "S",
    "T",
    "U",
    "V",
    "W",
    "X",
    "Y",
    "Z",
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    "7",
    "8",
    "9",
    "0",
    "Enter",
    "Esc",
    "Backspace",
    "Tab",
    "Space",
    "-",
    "=",
    "[",
    "]",
    "\\",
    "???", // Non-US #
    ";",
    "'",
    "`",
    ",",
    ".",
    "/",
    "Caps lock",
    "F1",
    "F2",
    "F3",
    "F4",
    "F5",
    "F6",
    "F7",
    "F8",
    "F9",
    "F10",
    "F11",
    "F12",
]

const codeToName = (code) => {
    if (code & RESERVED) {
        console.log ("RESERVED: " + code)
        // TODO
        return null
    }

    let flags = code >> 8
    let keyCode = code & 0x00ff

    console.log ("flags=" + flags + "; keyCode=" + keyCode)

    let baseKey = HIDCodes[keyCode]

    if (flags & CTRL_HELD) {
        baseKey = "Ctrl + " + baseKey
    }
    if (flags & LALT_HELD) {
        baseKey = "Alt + " + baseKey
    }
    if (flags & RALT_HELD) {
        baseKey = "AltGr + " + baseKey
    }
    if (flags & SHIFT_HELD) {
        baseKey = "Shift + " + baseKey
    }
    if (flags & GUI_HELD) {
        baseKey = "GUI + " + baseKey
    }

    return baseKey
}

module.exports = {
    "codeToName": codeToName,
}
