const map = L.map('map', {zoomControl: false}).setView([33.589886, -7.603869], 13);
let osm = null;

// Map Layer type
function defaultMode() {
    osm = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);
}
defaultMode();

// Pan the map to the given location
let searchMarker = null;
function goToLocation(location) {
    map.setView([location.lat, location.lon], 13);
    if(searchMarker) {
        searchMarker.remove();
    }

    searchMarker = L.marker([location.lat, location.lon], {
        riseOnHover: true,
        bounceOnAdd: false
    }).addTo(map);

    searchMarker.bindPopup("<strong>"+ location.name + "</strong>");
}

// Remove marker from map
function removeLocationMarker() {
    map.removeLayer(searchMarker);
}

// Get user device location
function getDeviceLocation() {
    let location;
    if(navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function (position) {
                location = {
                    lat: position.coords.latitude,
                    lon: position.coords.longitude
                }
                // goToDeviceLocation(location)
                console.log(location)
            },
            function (error) {
                console.log(error.message)
            }
        );
    } else {
        console.error("Geolocation is not supported by this browser");
    }
} // Browser Geolocation (not working with webView)
getDeviceLocation();

// Pan map to user device location
let currentLocationMarker = null;
function goToDeviceLocation(location) {
    map.setView([location.lat, location.lon], 13);
    if(currentLocationMarker) {
        currentLocationMarker.remove();
    }

    let icon = L.icon({
        iconUrl: '../images/gps_blue_dot.png',
        iconSize: [34, 34],
    });

    currentLocationMarker = L.marker([location.lat, location.lon], {
        riseOnHover: true,
        bounceOnAdd: false,
        icon: icon,
    }).addTo(map);
}