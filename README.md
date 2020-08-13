# GAENScan: scan for GAEN beacons in BLE range

GAENScan is a simple tool that leverages the existing Android BLE API to
repeatedly scan for beacons following the GAEN API (i.e., is listening
for GAEN RPIs). The app registers a listener that actively receives all RPIs 
and then aggregates the information on an ugly user interface.

This app enables users to get a quick overview of active GAEN devices in BLE
range. The app, in essence, implements a filter on BLE beacons and displays the
interesting beacons (RPIs). The same information is available to many generic
BLE scanners that are already available openly. The key difference is that
GAENScan does the filtering in the app (and ignores all other beacons).

Note that the app displays some simple statistics on the received RPIs but
does not track/correlate RPI rotations, each RPI is considered a unique device.


## What GAENScan is:

* GAENScan aggregates information for equal RPIs
* GAENScan displays statistics about packet loss and signal strength
* GAENScan demonstrates what an arbitrary *foreground* app can receive
  (background apps will receive much less data and processing time)


## What GAENScan can be used for:

* GAENScan allows a quick estimation on how many GAEN apps are in BLE range
* GAENScan shows how many RPIs are sent by devices in BLE range and received
  on this device via BLE


## What GAENScan is NOT:

* GAENScan cannot be used to identify people
* GAENScan cannot be used to deanonymize people
* GAENScan cannot be used to follow users
* GAENScan cannot correlate and combine RPIs when they are rotated (i.e., each
  unique RPI counts as unique user)
* GAENScan cannot assess the distance to other users because it lacks the
  decryption keys for the information in the tokens (i.e,. the sent signal
  strength is not known)


## Implementation

Scanning happens at low frequency by setting 
`ScanSettings.SCAN_MODE_LOW_LATENCY` in 
`ch/epfl/hexhive/gaenscan/MainActivity.java`. This means that GAENScan can
only receive beacons while in foreground (otherwise it would be restricted to
512ms windows every 5120ms). GAENScan filters for a specific UUID:
`0000fd6f-0000-1000-8000-00805f9b34fb` that selects only GAEN beacons. More
details are in the (simple) code.

Check out the repository, build the APK in Android Studio, and install it on
your Android device. The app was a quick hack for us to monitor active devices
during experiments. The main purpose is to give a quick count of active
devices in BLE range.


## License

The code is licensed under the Apache license, version 2.0.
The author is [Mathias Payer](mailto:mathias.payer@nebelwelt.net).

