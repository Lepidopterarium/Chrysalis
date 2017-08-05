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
    - [chrysalis.command](#chrysaliscommand)
        - [history-append! [item]](#history-append-item)
        - [run [command args event]](#run-command-args-event)
        - [history [processor(*optional*)]](#history-processoroptional)

<!-- markdown-toc end -->

----


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

Selects device based on its serial number. Retrieves description of device matching the passed serial number  and writes it to `:device/current` in app-db.


[↳ definition](/src/chrysalis/device.cljs#L147-L148)

---



## chrysalis.command

Defined in [command.cljs](/src/chrysalis/command.cljs).

---


### history-append! [item]

Appends an item (generally an executed command) to `:command/history` in app-db. `:command/history` stores the last 50 items added; older ones are dropped.

[↳ definition](/src/chrysalis/command.cljs#L99-L100)

---


### run [command args event]

Sends a command and its arguments to hardware. 
The command is formatted and written to the serial write buffer. Afterwards, the buffers, including the write buffer, are drained and the command is written to `:command/queue` in app-db. Command responses are received asynchronously. Whenever a response is received its paired up with its command and stored in app-db's `:command/history`. `run` also takes an `event`, which is dispatched when a device response is processed. Generally this event triggers Chrysalis to update its UI. It is also used to trigger processes such as printing a command's response to the REPL.

[↳ definition](/src/chrysalis/command.cljs#L102-L103)

---


### history [processor(*optional*)]

Returns the command and response history. An optional processor can be provided to format the data given application requirements. For example: While the REPL might present a nicely formatted, rich history, Wire Traffic Spy's output is relatively minimal.

[↳ definition](/src/chrysalis/command.cljs#L105-L112)

