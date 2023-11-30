package com.salmane.mapxplorer.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;

public class LeafletMapController {

    @FXML
    public WebView webview;
    public WebEngine engine;
    @FXML
    public TextField searchbar;
    private AutoCompletionBinding<String> autoCompletionBinding;
    private HashSet<String> possibleSuggestions;

    private void handleSearchEvent(ActionEvent event) {
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
                                    + "q=" + searchbar.getText()
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

        System.out.println(response.body());
    }

    public void initialize() {

        searchbar.setOnAction(this::handleSearchEvent );

//        TextFields.bindAutoCompletion(searchbar, possibleSuggestions);

        engine = webview.getEngine();
        webview.setCache(true);
        engine.load(String.valueOf(getClass().getResource("/com/salmane/mapxplorer/javascript/index.html")));
    }

}