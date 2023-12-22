package com.salmane.mapxplorer.model;

import com.salmane.mapxplorer.controller.LocationController;
import com.salmane.mapxplorer.controller.SidebarController;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;

public class DataManager {
    private static DataManager instance;
    private SidebarController sidebarController;
    private LocationController locationController;
    private TextField latitude;
    private TextField longitude;
    private WebEngine engine = null;
    private Location myLocation = null;


    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public SidebarController getSidebarController() {
        return sidebarController;
    }
    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    public LocationController getLocationController() {
        return locationController;
    }
    public void setLocationController(LocationController locationController) {
        this.locationController = locationController;
    }

    public void setLatLonFields(TextField latitude, TextField longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public TextField[] getLatLonFields() {
        return new TextField[]{latitude, longitude};
    }

    public WebEngine getEngine() {
        return engine;
    }
    public void setEngine(WebEngine engine) {
        this.engine = engine;
    }

    public Location getMyLocation() {
        return myLocation;
    }
    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }
}
