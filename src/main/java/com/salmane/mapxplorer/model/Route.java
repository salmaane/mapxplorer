package com.salmane.mapxplorer.model;

public class Route {
    private Integer destinationIndex;
    private Integer originIndex;
    private Integer distanceMeters;
    private String duration;
    private String staticDuration;
    private String condition;


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
    public Integer getDestinationIndex() {
        return destinationIndex;
    }
    public void setDestinationIndex(Integer destinationIndex) {
        this.destinationIndex = destinationIndex;
    }
    public Integer getOriginIndex() {
        return originIndex;
    }
    public void setOriginIndex(Integer originIndex) {
        this.originIndex = originIndex;
    }
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
}
