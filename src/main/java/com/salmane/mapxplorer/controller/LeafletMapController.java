package com.salmane.mapxplorer.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salmane.mapxplorer.model.Location;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LeafletMapController {

    @FXML
    public WebView webview;
    public WebEngine engine;
    @FXML
    public TextField searchbar;
    private final Gson gson = new Gson();
    private Timer timer = new Timer();
    private ArrayList<Location> possibleSuggestions;


    private void handleSearchEvent(KeyEvent event) {
        timer.cancel();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                KeyCode eventCode = event.getCode();
                if (eventCode == KeyCode.TAB || eventCode == KeyCode.ALT || eventCode == KeyCode.CONTROL
                        || eventCode == KeyCode.SHIFT || Objects.equals(searchbar.getText(), "")
                ) {
                    return;
                }

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest getRequest = null;
                HttpResponse<String> response = null;

                String format = "json";
                String limit = "5";
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

                Type locationType = new TypeToken<ArrayList<Location>>() {
                }.getType();
                possibleSuggestions = gson.fromJson(response.body(), locationType);

//                TextFields.bindAutoCompletion(searchbar, possibleSuggestions);

                System.out.println(possibleSuggestions.get(0).getDisplay_name());
            }
        }, 500);
    }

    private void handleGoToLocation(ActionEvent event) {
        engine.executeScript("goToLocation(" + gson.toJson(possibleSuggestions.get(0), Location.class) + ")");
    }

    public void initialize() {

        searchbar.setOnKeyReleased(this::handleSearchEvent);
        searchbar.setOnAction(this::handleGoToLocation);

        engine = webview.getEngine();
        webview.setCache(true);
        engine.load(String.valueOf(getClass().getResource("/com/salmane/mapxplorer/javascript/index.html")));
    }

}