package com.salmane.mapxplorer.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.salmane.mapxplorer.model.DataManager;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class MainController {
    public AnchorPane mainAnchorPane;
    @FXML
    public TextField apiKeyTextField;
    @FXML
    public Button proceedButton;
    @FXML
    public Text errorText;
    @FXML
    public ProgressIndicator spinner;

    @FXML
    public void handleProceedToMapClick(ActionEvent event) throws IOException {
        proceedButton.setText("");
        spinner.setVisible(true);

        Task<String> checkValidKeyTask = checkValidKey(apiKeyTextField.getText());

        checkValidKeyTask.setOnSucceeded((workerStateEvent) -> {
            String key = checkValidKeyTask.getValue();

            if(!Objects.equals(key, "\"OK\"")) {
                errorText.setVisible(true);
                proceedButton.setText("Proceed to map");
                spinner.setVisible(false);
            } else {
                DataManager.setGoogleApiKey(apiKeyTextField.getText());
                try {
                    new SwitchSceneController(event, "home.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        new Thread(checkValidKeyTask).start();
    }

    private Task<String> checkValidKey(String key) {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                HttpResponse<String> response = null;
                try {
                    HttpRequest getRequest = HttpRequest.newBuilder()
                            .uri(new URI(
                                    "https://maps.googleapis.com/maps/api/place/autocomplete/json?"
                                            + "key=" + key.trim()
                                            + "&input=" + "casa"
                            )).GET()
                            .build();
                    response = HttpClient.newHttpClient().send(getRequest, HttpResponse.BodyHandlers.ofString());

                } catch (InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }


                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                return jsonResponse.get("status").toString();
            }
        };
    }
}