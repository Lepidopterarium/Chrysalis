window.Chrysalis = require("./lib/Chrysalis/index")

Chrysalis.device.detect ()

Chrysalis.once ("device-detected", (devices) => {
    Chrysalis.device.open (devices[0])
})

Chrysalis.once ("device-ready", () => {
    Chrysalis.commands.version().then((version) => {
        $("#device").html ("<pre>" + version.device.manufacturer + "/" + version.device.product + "</pre>")
    })
})
