const map = L.map('map', {zoomControl: false}).setView([33.589886, -7.603869], 13);
let osm = null;

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
getDeviceLocation();

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
function placeMarkers(locations, myLocation, radius) {
    if(circle) circle.remove();
    if(markers) markers.map(marker => marker.remove())

    circle = L.circle([myLocation.location.latitude, myLocation.location.longitude], {
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
        let marker =  L.marker([location.location.latitude, location.location.longitude], {
            riseOnHover: true,
            bounceOnAdd: true,
            icon: greenIcon,
            title: location.displayName.text,
        }).on('click', function () {
            LeafletMapController.handleMarkerClick(JSON.stringify(location));
        }).addTo(map);

        return marker;
    });

    goToDeviceLocation(myLocation, undefined);
    map.fitBounds(circle.getBounds());

}
function removePLacesMarkers() {
    if(markers) markers.map(marker => marker.remove());
    if(circle) circle.remove();
}
