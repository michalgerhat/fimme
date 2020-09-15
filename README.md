# Fimme - navigation made simple

Fimme is meant to provide navigation in the most simplified way, displaying just an arrow pointed to a direction and distance in meters. Whether you need to meet with a friend at a music festival or locate a treasure that you previously buried on a remote island, Fimme will show you the way.

## Current - offline functionality

* Activity with list of saved places. User selects a place to get navigated to.
* Using the device's motion sensors as compass along with coordinates provided by location services, Fimme will point in the direction of the selected place.
* User can add his own places - either fill out the GPS coordinates or use their device's current GPS coordinates.
* Places are saved to and loaded from a JSON file on the device's internal storage.
* Places can be shared via a link and saved upon clicking the link.
* Places ListView is using a custom Adapter, allowing for deleting and sharing saved places.

## Todo - online functionality

* WebSocket communication module implementing protocol of [fimme-server](https://github.com/michalgerhat/fimme-server).
* GUI to support everything - login, friends list, friend requests, connection.
* Saving login info using AccountManager.
