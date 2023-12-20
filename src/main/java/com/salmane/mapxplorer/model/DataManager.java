package com.salmane.mapxplorer.model;

import com.salmane.mapxplorer.controller.LocationController;
import com.salmane.mapxplorer.controller.SidebarController;
import javafx.scene.control.TextField;

public class DataManager {
    private static DataManager instance;
    private SidebarController sidebarController;
    private LocationController locationController;
    private TextField latitude;
    private TextField longitude;

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
}
