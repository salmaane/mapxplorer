package com.salmane.mapxplorer.model;

public class TablePlaceInfo {
    private String name;
    private String distance;

    public TablePlaceInfo(String name, String distance) {
        this.name = name;
        this.distance = distance;
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
}
