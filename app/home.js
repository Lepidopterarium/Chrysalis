window.Chrysalis = require("../lib/Chrysalis/index")

Chrysalis.hardware.loadAll ()
Chrysalis.device.detect ()

Chrysalis.on ("device-detected", (device) => {
    var hw = Chrysalis.hardware.find (device)
    $("#device").append ("<div class='col-sm-6'><div class='card'>" +
                         "<div class='card-block'>" +
                         "<div class='card-text'><img class='' src='../lib/" + hw.assets.logo + "' alt='...'></div>" +
                         "</div><div class='card-footer text-muted'>" +
                         "<button type='button' class='btn btn-primary chrysalis-device-select' data-device='" + hw.meta.id + "'>Select</button>" +
                         "</div></div></div>")
})

$(document).on ("click", ".chrysalis-device-select", (event) => {
    console.log($(event.currentTarget).data('device'))
    //Chrysalis.device.open ("/dev/ttyACM0")
})

Chrysalis.on ("device-ready", () => {
    Chrysalis.commands.version().then((version) => {
        console.log (version)
    })
})
