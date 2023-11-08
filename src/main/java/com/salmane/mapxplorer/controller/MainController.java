package com.salmane.mapxplorer.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {
    public AnchorPane mainAnchorPane;

    @FXML
    public void handleProceedToMapClick(ActionEvent event) throws IOException {
        new SwitchSceneController(event, "home.fxml");
    }
}