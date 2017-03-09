var c = 0;
var r = 0;

document.getElementById("up").addEventListener("click", function() {
    keySwitch(r, c, 'off');
    r = decrementVar(r, 3);
    document.getElementById("debug_r").innerHTML = r;
    keySwitch(r, c, 'on');
});

document.getElementById("left").addEventListener("click", function() {
    keySwitch(r, c, 'off');
    c = decrementVar(c, 15);
    document.getElementById("debug_c").innerHTML = c;
    keySwitch(r, c, 'on');
});

document.getElementById("right").addEventListener("click", function() {
    keySwitch(r, c, 'off');
    c = incrementVar(c, 15);
    document.getElementById("debug_c").innerHTML = c;
    keySwitch(r, c, 'on');
});

document.getElementById("down").addEventListener("click", function() {
    keySwitch(r, c, 'off');
    r = incrementVar(r, 3); 
    document.getElementById("debug_r").innerHTML = r;
    keySwitch(r, c, 'on');
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


function keySwitch(r, c, keyState) {
    // Get the Object by ID


    var string_array = ["R", r, "C", c];

    var keyCode = string_array.join("");

    var a = document.getElementById("svgObject");
    // Get the SVG document inside the Object tag
    var svgDoc = a.contentDocument;
    // Get one of the SVG items by ID;


    var svgFill = svgDoc.getElementById(keyCode.concat("_F"));
    var svgStroke = svgDoc.getElementById(keyCode.concat("_S"));
    // Set the colour to something else

    if (keyState == 'on') {
    svgFill.setAttribute("fill", "gray");
    svgStroke.setAttribute("stroke-width", "5");
    }
    else {
        svgFill.setAttribute("fill", "#FFF");
        svgStroke.setAttribute("stroke-width", "0.8");        
    };
};

