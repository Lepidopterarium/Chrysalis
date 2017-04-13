const serialport = require('serialport')
const marked = require('marked');

var Focus = require("./lib/Focus/index")

var port = new serialport ("/dev/ttyACM0")

port.on('open', function() {
    port.flush()
    port.drain()
    port.read() // dummy read, no idea why it is needed...
});

Focus.commands.version(port).then((version) => {
    $("#device").html ("<pre>" + version.device.manufacturer + "/" + version.device.product + "</pre>")
})
