window.Chrysalis = require("../lib/Chrysalis/index")

Chrysalis.hardware.loadAll ()
Chrysalis.device.detect ()

Chrysalis.on ("device-detected", (device) => {
    Chrysalis.device.open (device.comName)
})

Chrysalis.on ("device-ready", () => {
    Chrysalis.commands.version().then((version) => {
        device = Chrysalis.hardware.select (version)
        $("#device").append ("<a href='#' class='btn btn-outline-secondary'><img src='../lib/" + device.assets.logo + "'></a>")
    })
})
