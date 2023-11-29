package com.salmane.mapxplorer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class LeafletMapController {

    @FXML
    public WebView webview;
    public WebEngine engine;
    @FXML
    public TextField searchbar;

    public void initialize() {
        engine = webview.getEngine();
        webview.setCache(true);
        engine.load(String.valueOf(getClass().getResource("/com/salmane/mapxplorer/javascript/index.html")));
    }

}