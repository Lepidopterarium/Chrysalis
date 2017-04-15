"use strict";

window.Chrysalis = require("../lib/Chrysalis/index");

Chrysalis.hardware.loadAll();
Chrysalis.device.detect();

Chrysalis.on("device-detected", function (device) {
    var hw = Chrysalis.hardware.find(device);
    $("#device").append("<div class='col-sm-6'><div class='card'>" + "<div class='card-block'>" + "<div class='card-text'><img class='' src='../lib/" + hw.assets.logo + "' alt='...'></div>" + "</div><div class='card-footer text-muted'>" + "<button type='button' class='btn btn-primary chrysalis-device-select' data-device='" + device.comName + "'>Select</button>" + "</div></div></div>");
});

$(document).on("click", ".chrysalis-device-select", function (event) {
    var device = $(event.currentTarget).data('device');
    Chrysalis.device.open(device);
});

Chrysalis.on("device-ready", function () {
    Chrysalis.commands.version().then(function (version) {
        console.log(version);
    });
});