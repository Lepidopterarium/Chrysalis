# Terminology
The purpose of this document is to formally define terminology as it is going to be used for Chrysalis and Kaleidoscope to ensure coherent naming conventions in code and documentation and simplify discussion of design decisions.


## Device package
A device package is a collection of files provided by a hardware developer to enable support of their device in Chrysalis. A device package requires inclusion of a device definition and metadata, as well as assets such as keymap SVGs, logos or even custom plugins for device-specific functionality. A device package should also include at least one default device configuration.


## Device configuration
A device configuration describes the whole of a device's settings created in Chrysalis. Chrysalis should provide functionality to export and import device configurations that are easily shareable between users and adhere to a standardised single-file format. Default device configurations should be provided as part of device packages (e.g. for different language layouts).
