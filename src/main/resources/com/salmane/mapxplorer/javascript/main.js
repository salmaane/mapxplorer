
const map = L.map('map', {zoomControl: false}).setView([33.589886, -7.603869], 13);
let osm = null;

function defaultMode() {
    osm = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);
}

defaultMode();