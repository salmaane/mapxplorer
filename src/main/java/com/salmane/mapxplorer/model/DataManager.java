package com.salmane.mapxplorer.model;

import com.salmane.mapxplorer.controller.SidebarController;

public class DataManager {
    private static DataManager instance;
    private SidebarController sidebarController;

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

}
