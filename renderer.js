window.Focus = require("./lib/Focus/index")

Focus.detect ()

Focus.once ("detected", (devices) => {
    Focus.open (devices[0])
})

Focus.once ("ready", () => {
    Focus.commands.version().then((version) => {
        $("#device").html ("<pre>" + version.device.manufacturer + "/" + version.device.product + "</pre>")
    })
})
