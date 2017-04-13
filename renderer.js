window.Focus = require("./lib/Focus/index")

Focus.detect ().then ((devices) => {
    Focus.open (devices[0])

    Focus.commands.version().then((version) => {
        $("#device").html ("<pre>" + version.device.manufacturer + "/" + version.device.product + "</pre>")
    })
})
