package com.salmane.mapxplorer.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.salmane.mapxplorer.model.Location;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LocationController {
    private final Gson gson = new Gson();
    private WebEngine engine = null;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public LocationController(WebEngine engine) {
        this.engine = engine;
    }

    public void GoToLocation(Location location) {
        engine.executeScript("goToLocation(" + gson.toJson(location, Location.class) + ")");
    }

    public void goToDeviceLocation(MouseEvent event, Location myLocation) {
        myLocation = new Location();
        Location.Coords coords = myLocation.new Coords();
        coords.setLatitude(33.589886);
        coords.setLongitude(-7.603869);
        myLocation.setLocation(coords);
        engine.executeScript("goToDeviceLocation("+ gson.toJson(myLocation, Location.class) +")");
    }

    public Location removeLocationMarker(
            MouseEvent event,
            TextField searchbar,
            FontAwesomeIconView closeIcon,
            FontAwesomeIconView returnToLocationIcon,
            ListView<?> autocompleteList,
            Location activeLocation
    ) {
        if(activeLocation != null) {
            engine.executeScript("removeLocationMarker()");
        }
        searchbar.setText("");
        closeIcon.setVisible(false);
        returnToLocationIcon.setVisible(false);
        autocompleteList.setVisible(false);
        return null;
    }

    private Location[] getNearbyPlaces(
            String googleApiKey,
            String fields,
            String[] includedTypes,
            int maxResultCount,
            Double lat,
            Double lon,
            Double radius
    ) {
        String jsonBody = "{\n" +
                "  \"includedTypes\": [\"" + String.join( "\", \"", includedTypes) + "\"],\n" +
                "  \"maxResultCount\": " + maxResultCount +  ",\n" +
                "  \"locationRestriction\": {\n" +
                "    \"circle\": {\n" +
                "      \"center\": {\n" +
                "        \"latitude\": " + lat + ",\n" +
                "        \"longitude\": " + lon + " },\n" +
                "      \"radius\": " + radius + "\n" +
                "    }\n" +
                "  }\n" +
                "}";

        try {
            HttpRequest postRequest = null;
            postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://places.googleapis.com/v1/places:searchNearby"))
                    .header("Content-Type", "application/json")
                    .header("X-Goog-Api-Key", googleApiKey)
                    .header("X-Goog-FieldMask", fields)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray results = jsonObject.get("places").getAsJsonArray();

            return gson.fromJson(results, Location[].class);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

