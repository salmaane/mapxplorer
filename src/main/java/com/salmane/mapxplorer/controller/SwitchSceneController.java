package com.salmane.mapxplorer.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SwitchSceneController {

    private Parent root;
    private Stage stage;
    private Scene scene;

    public SwitchSceneController(ActionEvent event, String fxml) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/salmane/mapxplorer/fxml/"+fxml)));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        centerStage();
        stage.show();
    }

    private void centerStage() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        double newX = (screenWidth - stageWidth) / 2;
        double newY = (screenHeight - stageHeight) / 2;

        stage.setX(newX);
        stage.setY(newY);
    }

}
