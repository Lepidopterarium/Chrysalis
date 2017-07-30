# API

Documentation of Chrysalis' API functions.

**Work in progress. May include incorrect/outdated information.**



<!-- markdown-toc start - Don't edit this section. Run M-x markdown-toc-generate-toc again -->
**Table of Contents**

- [API](#api)
    - [chrysalis.device](#chrysalisdevice)
        - [detect!](#detect)
        - [list](#list)
        - [current](#current)
        - [select! [id]](#select-id)
        - [select-by-serial!](#select-by-serial)

<!-- markdown-toc end -->




## chrysalis.device

Defined in [device.cljs](/src/chrysalis/device.cljs).

---


### detect!

Scans connected devices and writes a (persistent) vector containing device descriptions of compatible devices to `:device/list` in app-db.

[↳ definition](/src/chrysalis/device.cljs#L135-L136)

---


### list

Returns `:device/list` from app-db.

[↳ definition](/src/chrysalis/device.cljs#L138-L139)

---


### current

Returns description of currently selected device.

[↳ definition](/src/chrysalis/device.cljs#L141-L142)

---


### select! [id]

Selects device based on its ID. Retrieves a description of device matching the passed device ID and writes it to `:device/current` in app-db.

[↳ definition](/src/chrysalis/device.cljs#L144-L145)

---


### select-by-serial!

Selects device based on its serial number. Retrieves description of device matching the passed serial number.

[↳ definition](/src/chrysalis/device.cljs#L147-L148)

---


