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

const IS_SYSCTL = 0b00000001
const IS_CONSUMER = 0b00000010
const SWITCH_TO_KEYMAP = 0b00000100
const IS_INTERNAL = 0b00001000

const LED_TOGGLE = 0b00000001 // Synthetic | INTERNAL | LED_TOGGLE
const IS_MACRO = 0b00100000 // Synthetic | IS_MACRO

const IS_MOUSE_KEY = 0b00010000 // Synthetic | IS_MOUSE_KEY
const KEY_MOUSE_BTN_L = 0x01 // Synthetic key
const KEY_MOUSE_BTN_M = 0x02 // Synthetic key
const KEY_MOUSE_BTN_R = 0x03 // Synthetic key

const KEY_MOUSE_UP       =   0b0000001
const KEY_MOUSE_DOWN     =   0b0000010
const KEY_MOUSE_LEFT     =   0b0000100
const KEY_MOUSE_RIGHT    =   0b0001000
const KEY_MOUSE_BUTTON   =   0b0010000
const KEY_MOUSE_WARP     =   0b0100000
const KEY_MOUSE_WARP_END =   0b0010000
const KEY_MOUSE_WHEEL    =   0b1000000

const OSM_FIRST = 0xc001
const OSM_LAST = OSM_FIRST + 7
const OSL_FIRST = OSM_LAST + 1
const OSL_LAST = OSL_FIRST + 7
const DUM_FIRST = OSL_LAST + 1
const DUM_LAST = DUM_FIRST + (8 << 8)
const DUL_FIRST = DUM_LAST + 1
const DUL_LAST = DUL_FIRST + (8 << 8)
const TD_FIRST = DUL_LAST + 1
const TD_LAST = TD_FIRST + 15
const LEAD_FIRST = TD_LAST + 1
const LEAD_LAST = LEAD_FIRST + 7
const CYCLE = LEAD_LAST + 1
const SYSTER = CYCLE + 1

const HIDCodes = [
    "[NoKey]",
    undefined, // Error: Rollover
    undefined, // Error: PostFail
    undefined, // Error: Undefined
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
    undefined, // Non-US #
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
    "Print Screen",
    "Scroll lock",
    "Pause",
    "Insert",
    "Home",
    "Page up",
    "Del",
    "End",
    "Page down",
    "Right arrow",
    "Left arrow",
    "Down arrow",
    "Up arrow",
    "Num lock",
    "Keypad /",
    "Keypad *",
    "Keypad -",
    "Keypad +",
    "Keypad Enter",
    "Keypad 1",
    "Keypad 2",
    "Keypad 3",
    "Keypad 4",
    "Keypad 5",
    "Keypad 6",
    "Keypad 7",
    "Keypad 8",
    "Keypad 9",
    "Keypad 0",
    "Keypad .",
    undefined, // Non-US /
    "Application",
    "Power",
    "Keypad =",
    "F13",
    "F14",
    "F15",
    "F16",
    "F17",
    "F18",
    "F19",
    "F20",
    "F21",
    "F22",
    "F23",
    "F24",
    "Execute",
    "Help",
    "Menu",
    "Select",
    "Stop",
    "Again",
    "Undo",
    "Cut",
    "Copy",
    "Paste",
    "Find",
    "Mute",
    "Volume up",
    "Volume down",
    undefined, // Locking Caps
    undefined, // Locking Num
    undefined, // Locking Scroll
    "Keypad ,",
    "Keypad Equal",
    undefined, // Int1
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined, // Int9
    undefined, // Lang1
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined, // Lang9
    undefined, // Alt Erase
    "SysRq",
    "Cancel",
    "Clear",
    "Prior",
    "Return",
    "Separator",
    undefined, // Out
    undefined, // Oper
    undefined, // Clear/Again
    undefined, // CrSel/Props
    undefined, // ExSel
    undefined, // Reserved
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined,
    undefined, // Reserved
    "Keypad 00",
    "Keypad 000",
    undefined, // Thousands separator
    undefined, // Decimal separator
    undefined, // Currency unit
    undefined, // Currency sub-unit
    "Keypad (",
    "Keypad )",
    "Keypad {",
    "Keypad }",
    "Keypad Tab",
    "Keypad Backspace",
    "Keypad A",
    "Keypad B",
    "Keypad C",
    "Keypad D",
    "Keypad E",
    "Keypad F",
    "Keypad XOR",
    "Keypad ^",
    "Keypad %",
    "Keypad <",
    "Keypad >",
    "Keypad &",
    "Keypad &&",
    "Keypad |",
    "Keypad ||",
    "Keypad :",
    "Keypad #",
    "Keypad Space",
    "Keypad @",
    "Keypad !",
    undefined, // Keypad Memory Store
    undefined, // Keypad Memory Recall
    undefined, // Keypad Memory Clear
    undefined, // Keypad Memory Add
    undefined, // Keypad Memory Subs
    undefined, // Keypad Memory Mult
    undefined, // Keypad Memory Divide
    "Keypad +/-",
    undefined, // Keypad Clear
    undefined, // Keypad Clear Entry
    undefined, // Keypad Binary
    undefined, // Keypad Octal
    undefined, // Keypad Decimal
    undefined, // Keypad Hexadecimal
    undefined, // Reserved
    undefined, // Reserved
    "Left Control",
    "Left Shift",
    "Left Alt",
    "Left GUI",
    "Right Control",
    "Right Shift",
    "Right Alt",
    "Right GUI",
]

const codeToName = (code) => {
    if (code == 65535) {
        return "[Transparent]"
    }

    if (code & RESERVED) {
        if (code >= OSM_FIRST && code <= OSM_LAST) {
            return "OSM(" + HIDCodes[(code - OSM_FIRST) + HIDCodes.indexOf ("Left Control")] + ")"
        }
        if (code >= OSL_FIRST && code <= OSL_LAST) {
            return "OSL(" + (code - OSL_FIRST) + ")"
        }

        // TODO
        return null
    }

    let flags = code >> 8
    let keyCode = code & 0x00ff

    if (flags & SYNTHETIC) {
        if (flags & IS_SYSCTL) {
            // TODO
            console.log ("SYSCTL: " + keyCode)
        } else if (flags & IS_CONSUMER) {
            // TODO
            console.log ("CONSUMER: " + keyCode)
        } else if (flags & SWITCH_TO_KEYMAP) {
            // TODO
            console.log ("KEYMAP: " + keyCode)
        } else if (flags & IS_INTERNAL) {
            if (flags & LED_TOGGLE)
                return "LEDEffectNext"
        } else if (flags & IS_MACRO) {
            return "M(" + keyCode + ")"
        } else if (flags & IS_MOUSE_KEY) {
            // TODO
            console.log ("MouseKey: " + keyCode)
        } else {
            // TODO
            console.log ("SYNTHETIC: " + keyCode)
        }

        return null
    }

    let baseKey = HIDCodes[keyCode]

    if (flags & CTRL_HELD) {
        baseKey = "LCTRL(" + baseKey + ")"
    }
    if (flags & LALT_HELD) {
        baseKey = "LALT(" + baseKey + ")"
    }
    if (flags & RALT_HELD) {
        baseKey = "RALT(" + baseKey + ")"
    }
    if (flags & SHIFT_HELD) {
        baseKey = "LSHIFT(" + baseKey + ")"
    }
    if (flags & GUI_HELD) {
        baseKey = "LGUI(" + baseKey + ")"
    }

    return baseKey
}

module.exports = {
    "codeToName": codeToName,
}
