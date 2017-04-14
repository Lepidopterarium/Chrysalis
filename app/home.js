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
        $("#device").html ("<pre>" + version.device.manufacturer + "/" + version.device.product + "</pre>")
    })
})
