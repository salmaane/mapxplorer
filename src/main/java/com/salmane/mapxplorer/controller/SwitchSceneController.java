package com.salmane.mapxplorer.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        stage.show();
    }

}
