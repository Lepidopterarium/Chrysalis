window.Chrysalis = require("../lib/Chrysalis/index")

Chrysalis.hardware.loadAll ()
Chrysalis.device.detect ()

Chrysalis.on ("device-detected", (device) => {
    var hw = Chrysalis.hardware.find (device)
    $("#device").append ("<div class='col-sm-6'><div class='card'>" +
                         "<div class='card-block'>" +
                         "<div class='card-text'><img class='' src='../lib/" + hw.assets.logo + "' alt='...'></div>" +
                         "</div><div class='card-footer text-muted'>" +
                         "<a href='#' class='btn btn-primary'>Select</a>" +
                         "</div></div></div>")
})


/*
Chrysalis.on ("device-ready", () => {
    Chrysalis.commands.version().then((version) => {
        device = Chrysalis.hardware.select (version)
        $("#device").append ("<a href='#' class='btn btn-outline-secondary'><img src='../lib/" + device.assets.logo + "'></a>")
    })
})
*/
