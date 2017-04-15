import React from 'react';
import ReactDOM from 'react-dom';

Chrysalis.hardware.loadAll ()
Chrysalis.device.detect ()

Chrysalis.on ("device-detected", (device) => {
    var hw = Chrysalis.hardware.find (device)
    ReactDOM.render(
            <div className="col-sm-6">
              <div className="card">
                <div className="card-block">
                  <div className="card-text">
                    <img src={ "../lib/" + hw.assets.logo } alt='...' />
                  </div>
                </div>
                <div className="card-footer text-muted">
                  <button type="button" className="btn btn-primary chrysalis-device-select"
                          data-device={ device.comName }>Select</button>
                </div>
              </div>
            </div>,
        document.getElementById('device')
    )
})

$(document).on ("click", ".chrysalis-device-select", (event) => {
    var device = $(event.currentTarget).data('device')
    Chrysalis.device.open (device)
})

Chrysalis.on ("device-ready", () => {
    Chrysalis.commands.version().then((version) => {
        console.log (version)
    })
})
