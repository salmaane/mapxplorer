package com.salmane.mapxplorer.model;

public class PlacePredictions {
    private String description;
    private String place_id;
    private String reference;
    private String[] types;
    public String getDescription() {
        return description;
    }
    public String getPlace_id() {
        return place_id;
    }
    public String getReference() {
        return reference;
    }
    public String[] getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return description;
    }
}
