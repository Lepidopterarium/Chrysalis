# API

Documentation on Chrysalis' API functions.

**Work in progress. May include incorrect/outdated information.**


## (ns chrysalis.device)

Defined in [device.cljs](/src/chrysalis/device.cljs).

[**(detect!)**](/src/chrysalis/device.cljs#L135-L136)
Scans connected devices and writes a (persistent) vector containing device descriptions of compatible devices to 
`:device/list` in app-db.

[**(list)**](/src/chrysalis/device.cljs#L138-L139)
Returns `:device/list` from app-db.

[**(current)**](/src/chrysalis/device.cljs#L141-L142)
Returns description of currently selected device.

[**(select! [id])**](/src/chrysalis/device.cljs#L144-L145)
Selects device based on its ID. Retrieves a description of device matching the passed device ID and writes it to `:device/current` in app-db.

[**(select-by-serial! [serial])**](/src/chrysalis/device.cljs#L147-L148)
Selects device based on its serial number. Retrieves description of device matching the passed serial number.

