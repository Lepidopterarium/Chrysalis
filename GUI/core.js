var c = 0;
var r = 0;
var keyCoord = "R0C0";


document.getElementById("up").addEventListener("click", function() {
    keySwitch(keyCoord, "off");
    r = decrementVar(r, 3);
    keyCoord = createCoordinates(r, c);
    document.getElementById("debug_r").innerHTML = r;
    keySwitch(keyCoord, "on", "grey");
});

document.getElementById("left").addEventListener("click", function() {
    keySwitch(keyCoord, "off");
    c = decrementVar(c, 15); 
    keyCoord = createCoordinates(r, c);
    document.getElementById("debug_c").innerHTML = c;
    keySwitch(keyCoord, "on", "grey");
});

document.getElementById("right").addEventListener("click", function() {
    keySwitch(keyCoord, "off");
    c = incrementVar(c, 15);
    keyCoord = createCoordinates(r, c);
    document.getElementById("debug_c").innerHTML = c;
    keySwitch(keyCoord, "on", "grey");
});

document.getElementById("down").addEventListener("click", function() {
    keySwitch(keyCoord, "off");
    r = incrementVar(r, 3);
    keyCoord = createCoordinates(r, c);
    document.getElementById("debug_r").innerHTML = r;
    keySwitch(keyCoord, "on", "grey");
});


function decrementVar(i, limit) {
    if (i == 0) {
        i = limit;
    } else {
        i -= 1;
    }
    return i;
};


function incrementVar(i, limit) {
    if (i == limit) {
        i = 0;
    } else {
        i += 1;
    }
    return i;
};


function createCoordinates(r, c) {

    // Create coordinate string
    var string_array = ["R", r, "C", c];
    var keyCoord = string_array.join("");
    return keyCoord;
};


function keySwitch(keyCoord, keyState, colour) {
    // Get the SVG document
    var a = document.getElementById("svgObject");
    var svgDoc = a.contentDocument;

    // Get SVG elements for fill and stroke of the current key
    var svgFill = svgDoc.getElementById(keyCoord.concat("_F"));
    var svgStroke = svgDoc.getElementById(keyCoord.concat("_S"));

    if (keyState == 'on') {
        // Turn key on
        svgFill.setAttribute("fill", colour);
        svgStroke.setAttribute("stroke-width", "5");
    } else {
        // Turn key off
        svgFill.setAttribute("fill", "#FFF");
        svgStroke.setAttribute("stroke-width", "0.8");
    };
};


function showEvtData(evt) {

    // Get event information
    var evtType = evt.type;
    var evtID = evt.target.getAttributeNS(null, "id");

    // Print event information
    document.getElementById("debug_event_type").innerHTML = evtType;
    document.getElementById("debug_event_element_ID").innerHTML = evtID;

    // Remove element type suffix
    var keyCoord = evtID.split("_")[0];


    // Switch key states
    if (evtType == "mouseover") {
        keySwitch(keyCoord, "on", "grey");
    } else if (evtType == "mouseout") {
        keySwitch(keyCoord, "off");
    } else if (evtType == "click") {
        keySwitch(keyCoord, "on", "red");
    };
};
