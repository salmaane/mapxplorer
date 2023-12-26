package com.salmane.mapxplorer.model;

public class TablePlaceInfo {
    private String name;
    private String distance;
    private Location place;
    private Route route;

    public TablePlaceInfo(String name, String distance, Location place, Route route) {
        this.name = name;
        this.distance = distance;
        this.place = place;
        this.route = route;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Location getPlace() {
        return place;
    }
    public void setPlace(Location place) {
        this.place = place;
    }
    public Route getRoute() {
        return route;
    }
    public void setRoute(Route route) {
        this.route = route;
    }
}
