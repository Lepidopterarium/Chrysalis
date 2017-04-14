window.Chrysalis = require("../lib/Chrysalis/index")

Chrysalis.device.isSupported = function (port) {
    if ((port.manufacturer == "Keyboardio") ||
        (port.manufacturer == "Shortcut"))
        return true
    return false
}

Chrysalis.device.detect ().catch (() => {
    console.log ("no devices found")
})

Chrysalis.once ("device-detected", (device) => {
    Chrysalis.device.open (device.comName)
})

Chrysalis.once ("device-ready", () => {
    Chrysalis.commands.version().then((version) => {
        Chrysalis.hardware.load(version)
        $("#device").html ("<a href='#' class='btn btn-outline-secondary'><img src='../lib/" + Chrysalis.device.meta.logo + "'></a>")
    })
})
