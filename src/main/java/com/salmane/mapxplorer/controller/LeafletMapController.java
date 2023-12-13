package com.salmane.mapxplorer.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salmane.mapxplorer.model.Location;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LeafletMapController {
    @FXML
    public WebView webview;
    public WebEngine engine;
    @FXML
    public TextField searchbar;
    @FXML
    public ListView<Location> autocompleteList;
    @FXML
    public FontAwesomeIconView closeIcon;
    @FXML
    public FontAwesomeIconView myLocationIcon;
    @FXML
    public Tooltip myLocationTooltip;
    @FXML
    public FontAwesomeIconView returnToLocationIcon;
    private final Gson gson = new Gson();
    private Timer timer = new Timer();
    private ArrayList<Location> possibleSuggestions;
    private Location activeLocation = null;
    private Location myLocation;

    public void initialize() {
        searchbar.setOnKeyReleased(this::handleSearchEvent);
        autocompleteList.setOnMouseClicked(this::handleAutocompleteSelection);

        PauseTransition delay = new PauseTransition(Duration.seconds(0.2));
        delay.setOnFinished(e -> autocompleteList.setVisible(false));
        searchbar.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if(activeLocation != null) searchbar.setText(activeLocation.getName());
                delay.playFromStart();
            } else {
                handleSearchEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UNDEFINED, false, false, false, false));
            }
        });

        closeIcon.setOnMouseClicked(this::removeLocationMarker);
        returnToLocationIcon.setOnMouseClicked(event -> GoToLocation(activeLocation));

        myLocationTooltip.setShowDelay(new Duration(100));
        myLocationTooltip.setShowDuration(new Duration(900));
        myLocationIcon.setOnMouseClicked(this::goToDeviceLocation);

        engine = webview.getEngine();
        webview.setCache(true);
        engine.load(String.valueOf(getClass().getResource("/com/salmane/mapxplorer/javascript/index.html")));
    }

    private void handleSearchEvent(KeyEvent event) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                KeyCode eventCode = event.getCode();
                if (eventCode == KeyCode.TAB || eventCode == KeyCode.ALT || eventCode == KeyCode.CONTROL
                        || eventCode == KeyCode.SHIFT || eventCode == KeyCode.ENTER
                ) {
                    return;
                }

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest getRequest = null;
                HttpResponse<String> response = null;

                String format = "json";
                String limit = "9";
                String addressdetails = "1";
                String extratags = "0";
                String namedetails = "0";

                try {
                    getRequest = HttpRequest.newBuilder()
                            .uri(new URI(
                                    "https://nominatim.openstreetmap.org/search?"
                                            + "q=" + searchbar.getText().replaceAll("\\s","")
                                            + "&format=" + format
                                            + "&limit=" + limit
                                            + "&addressdetails=" + addressdetails
                                            + "&extratags=" + extratags
                                            + "&namedetails=" + namedetails
                            )).GET()
                            .build();

                    response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

                } catch (IOException | InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                Type locationType = new TypeToken<ArrayList<Location>>(){}.getType();
                possibleSuggestions = gson.fromJson(response.body(), locationType);

                Platform.runLater(() -> {
                    if (possibleSuggestions.isEmpty()) {
                        autocompleteList.setVisible(false);
                        return;
                    }
                    autocompleteList.getItems().clear();
                    autocompleteList.getItems().addAll(possibleSuggestions);
                    autocompleteList.setVisible(true);
                });
            }
        }, 500);
    }

    private void GoToLocation(Location location) {
        engine.executeScript("goToLocation(" + gson.toJson(location, Location.class) + ")");
    }

    private void handleAutocompleteSelection(MouseEvent event) {
        Location selectedLocation = autocompleteList.getSelectionModel().getSelectedItem();
        if (selectedLocation != null) {
            searchbar.setText(selectedLocation.getName());
            autocompleteList.setVisible(false);
            activeLocation = selectedLocation;
            closeIcon.setVisible(true);
            returnToLocationIcon.setVisible(true);
            GoToLocation(selectedLocation);
        }
    }

    private void removeLocationMarker(MouseEvent event) {
        if(activeLocation != null) {
            engine.executeScript("removeLocationMarker()");
            activeLocation = null;
        }
        searchbar.setText("");
        closeIcon.setVisible(false);
        returnToLocationIcon.setVisible(false);
        autocompleteList.setVisible(false);
    }

    private void goToDeviceLocation(MouseEvent event) {
        myLocation = new Location();
        myLocation.setLat(33.589886);
        myLocation.setLon(-7.603869);
        engine.executeScript("goToDeviceLocation("+ gson.toJson(myLocation, Location.class) +")");
    }
}