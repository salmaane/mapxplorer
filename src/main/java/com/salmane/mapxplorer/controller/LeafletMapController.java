package com.salmane.mapxplorer.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class LeafletMapController {

    @FXML
    public WebView webview;
    public WebEngine engine;

    public void initialize() {
        engine = webview.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.load(String.valueOf(getClass().getResource("/com/salmane/mapxplorer/javascript/index.html")));
    }

}