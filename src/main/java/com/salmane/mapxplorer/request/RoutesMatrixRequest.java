package com.salmane.mapxplorer.request;


import java.util.List;

public class RoutesMatrixRequest {
    private List<Waypoint> origins;
    private List<Waypoint> destinations;
    private String travelMode;
    private String routingPreference;

    public void setOrigins(List<Waypoint> origins) {
        this.origins = origins;
    }
    public void setDestinations(List<Waypoint> destinations) {
        this.destinations = destinations;
    }
    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }
    public void setRoutingPreference(String routingPreference) {
        this.routingPreference = routingPreference;
    }
    public List<Waypoint> getOrigins() {
        return origins;
    }
    public List<Waypoint> getDestinations() {
        return destinations;
    }
    public String getTravelMode() {
        return travelMode;
    }
    public String getRoutingPreference() {
        return routingPreference;
    }

    public static class Waypoint {
        private WaypointDetails waypoint;
        private RouteModifiers routeModifiers;
        public void setWaypoint(WaypointDetails waypoint) {
            this.waypoint = waypoint;
        }
        public void setRouteModifiers(RouteModifiers routeModifiers) {
            this.routeModifiers = routeModifiers;
        }
        public WaypointDetails getWaypoint() {
            return waypoint;
        }
        public RouteModifiers getRouteModifiers() {
            return routeModifiers;
        }

        public static class WaypointDetails {
            private wpLocation location;
            private String placeId;

            public void setLocation(wpLocation location) {
                this.location = location;
            }
            public void setPlaceId(String placeId) {
                this.placeId = placeId;
            }
            public wpLocation getLocation() {
                return location;
            }
            public String getPlaceId() {
                return placeId;
            }

            public static class wpLocation {
                private LatLng latLng;
                public static class LatLng {
                    private Double latitude;
                    private Double longitude;

                    public Double getLatitude() {
                        return latitude;
                    }
                    public void setLatitude(Double latitude) {
                        this.latitude = latitude;
                    }
                    public Double getLongitude() {
                        return longitude;
                    }
                    public void setLongitude(Double longitude) {
                        this.longitude = longitude;
                    }
                }
                public LatLng getLatLng() {
                    return latLng;
                }
                public void setLatLng(LatLng latLng) {
                    this.latLng = latLng;
                }
            }
        }
        public static class RouteModifiers {
            private Boolean avoid_ferries;
            public Boolean getAvoid_ferries() {
                return avoid_ferries;
            }
            public void setAvoid_ferries(Boolean avoid_ferries) {
                this.avoid_ferries = avoid_ferries;
            }
        }

    }
}
