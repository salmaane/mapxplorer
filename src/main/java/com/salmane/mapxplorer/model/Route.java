package com.salmane.mapxplorer.model;

public class Route {
    private Integer distanceMeters;
    private String duration;
    private String staticDuration;
    private Polyline polyline;

    public class Polyline {
        private String encodedPolyline;

        public String getEncodedPolyline() {
            return encodedPolyline;
        }
    }

    public Integer getDistanceMeters() {
        return distanceMeters;
    }
    public void setDistanceMeters(Integer distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getStaticDuration() {
        return staticDuration;
    }
    public void setStaticDuration(String staticDuration) {
        this.staticDuration = staticDuration;
    }
    public Polyline getPolyline() {
        return polyline;
    }
    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}
