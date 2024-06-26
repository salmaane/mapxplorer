const map = L.map('map', {zoomControl: false}).setView([33.589886, -7.603869], 13);
let osm = null;

let neabySearchMarker = null;
map.on('click', function (e) {
    var latitude = e.latlng.lat;
    var longitude = e.latlng.lng;

    SidebarController.handleCoordsChange(latitude, longitude);

    if(neabySearchMarker) neabySearchMarker.remove();
    let icon = L.icon({
        iconUrl: '../images/x-marker.png',
        iconSize: [26, 26],
    });
    neabySearchMarker = L.marker([latitude, longitude], {
        riseOnHover: true,
        bounceOnAdd: false,
        icon: icon,
    }).addTo(map);
    neabySearchMarker.bindPopup("Nearby search coords");
});

let zoomIn = () => map.zoomIn();
let zoomOut = () => map.zoomOut();

// Map Layer type
osmLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);

googleSatelliteLayer = L.tileLayer('http://{s}.google.com/vt/lyrs=s,h&x={x}&y={y}&z={z}',{
    subdomains:['mt0','mt1','mt2','mt3']
});

function switchToGoogleSatelliteLayer() {
    map.removeLayer(osmLayer);
    map.addLayer(googleSatelliteLayer);
}
function switchToOSMLayer() {
    map.removeLayer(googleSatelliteLayer);
    map.addLayer(osmLayer);
}

// Pan the map to the given location
let searchMarker = null;
function goToLocation(location) {
    map.flyTo([location.location.latitude, location.location.longitude], 13);
    if(searchMarker) {
        searchMarker.remove();
    }

    searchMarker = L.marker([location.location.latitude, location.location.longitude], {
        riseOnHover: true,
        bounceOnAdd: false,
        title: location.displayName.text,
    }).on('click', function () {
        LeafletMapController.handleMarkerClick(JSON.stringify(location));
    }).addTo(map);
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
                    latitude: position.coords.latitude,
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

// Pan map to user device location
let currentLocationMarker = null;
function goToDeviceLocation(location, zoom = 13) {
    map.flyTo([location.location.latitude, location.location.longitude], zoom);
    if(currentLocationMarker) {
        currentLocationMarker.remove();
    }

    let icon = L.icon({
        iconUrl: '../images/gps_blue_dot.png',
        iconSize: [34, 34],
    });

    currentLocationMarker = L.marker([location.location.latitude, location.location.longitude], {
        riseOnHover: true,
        bounceOnAdd: false,
        icon: icon,
    }).addTo(map);
}

// Place nearby places markers on the map with a circle representing the area of those places
let circle = null;
let markers = [];
let locationCircles = [];
function placeMarkers(locations, center, radius, markerRadius) {
    if(circle) circle.remove();
    if(markers) markers.map(marker => marker.remove());
    if(locationCircles) locationCircles.map(circle => circle.remove());

    circle = L.circle([center.location.latitude, center.location.longitude], {
        radius: radius,
        fillColor: '#65B741',
        fillOpacity: 0.12,
        color:'#163020',
        weight: 1,
    }).addTo(map);

    var greenIcon = new L.Icon({
        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41]
    });

    markers = locations.map(location => {
        return L.marker([location.location.latitude, location.location.longitude], {
            riseOnHover: true,
            bounceOnAdd: true,
            icon: greenIcon,
            title: location.displayName.text,
        }).on('click', function () {
            LeafletMapController.handleMarkerClick(JSON.stringify(location));
        }).addTo(map);
    });

    locationCircles = locations.map(location => {
        return L.circle([location.location.latitude, location.location.longitude], {
            radius: markerRadius,
            fillColor: '#0952ff',
            fillOpacity: 0.12,
            color: '#163020',
            weight: 1,
        }).addTo(map);
    });

    map.fitBounds(circle.getBounds());
}
function removePLacesMarkers() {
    if(markers) markers.map(marker => marker.remove());
    if(circle) circle.remove();
}
