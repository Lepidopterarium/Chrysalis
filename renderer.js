window.Focus = require("./lib/Focus/index")

Focus.open ("/dev/ttyACM0")
Focus.commands.version().then((version) => {
    $("#device").html ("<pre>" + version.device.manufacturer + "/" + version.device.product + "</pre>")
})
