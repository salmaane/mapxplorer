package com.salmane.mapxplorer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MapXplorerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MapXplorerApp.class.getResource("fxml/mainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        stage.setTitle("Mapxplorer");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/logos/logo.png")));
        stage.getIcons().add(icon);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}