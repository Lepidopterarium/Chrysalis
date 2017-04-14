window.Chrysalis = require("../lib/Chrysalis/index")

Chrysalis.hardware.loadAll ()
Chrysalis.device.detect ()

Chrysalis.on ("device-detected", (device) => {
    var hw = Chrysalis.hardware.find (device)
    $("#device").append ("<a href='#' class='btn btn-outline-secondary'><img src='../lib/" + hw.assets.logo + "'></a>")
})


/*
Chrysalis.on ("device-ready", () => {
    Chrysalis.commands.version().then((version) => {
        device = Chrysalis.hardware.select (version)
        $("#device").append ("<a href='#' class='btn btn-outline-secondary'><img src='../lib/" + device.assets.logo + "'></a>")
    })
})
*/
