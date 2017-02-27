const serialport = require('serialport')
const marked = require('marked');

var port = new serialport ("/dev/ttyACM0")

function appendBox(object, text) {
    var box = document.createElement ("pre")
    var node = document.createTextNode (text)

    box.appendChild (node)
    object.appendChild (box)
}

function processResult (res) {
    return res.substring(0, res.length - 4)
}

function sendCommand () {
    var cmd = document.getElementById("command").value
    talk (cmd)
    document.getElementById("command").value = ""
    return false
}

function talk(command) {
    port.write (command + "\n", function () {
        port.drain (function (e) {
            window.setTimeout (function () {
                var o = document.getElementById('output')
                appendBox (o, processResult (port.read().toString()))
            }, 100)
        })
    })
}

port.on('open', function() {
    port.flush()
    port.drain()
    port.read() // dummy read, no idea why it is needed...
});
