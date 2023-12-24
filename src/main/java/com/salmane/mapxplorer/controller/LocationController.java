package com.salmane.mapxplorer.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.salmane.mapxplorer.model.DataManager;
import com.salmane.mapxplorer.model.Location;
import com.salmane.mapxplorer.model.Route;
import com.salmane.mapxplorer.request.RoutesMatrixRequest;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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
        DataManager.getInstance().setMyLocation(myLocation);
        engine.executeScript("goToDeviceLocation("+ gson.toJson(myLocation, Location.class) +")");
        DataManager.getInstance().getSidebarController().enableControls();
        DataManager.getInstance().getLatLonFields()[0].setText(myLocation.getLocation().getLatitude().toString());
        DataManager.getInstance().getLatLonFields()[1].setText(myLocation.getLocation().getLongitude().toString());
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

    public Location[] getNearbyPlaces(
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
                "      \"radius\": " + radius * 1_000 + "\n" +
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

    public Route[] getRoutes(
            String googleApiKey,
            String fields,
            Double lat,
            Double lon,
            Location[] places
    ) {
        RoutesMatrixRequest routesMatrixRequest = createRoutesMatrixRequest(lat, lon, places);
        String jsonBody = gson.toJson(routesMatrixRequest, RoutesMatrixRequest.class);
        System.out.println(jsonBody);
        try {
            HttpRequest postRequest = null;
            postRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://routes.googleapis.com/distanceMatrix/v2:computeRouteMatrix"))
                    .header("Content-Type", "application/json")
                    .header("X-Goog-Api-Key", googleApiKey)
                    .header("X-Goog-FieldMask", fields)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

            return gson.fromJson(response.body(), Route[].class);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private RoutesMatrixRequest createRoutesMatrixRequest(Double lat, Double lon, Location[] places) {
        RoutesMatrixRequest routesMatrixRequest = new RoutesMatrixRequest();

        // Create and set origin
        RoutesMatrixRequest.Waypoint origin = getWaypoint(lat, lon);

        // Create and set destinations
        List<RoutesMatrixRequest.Waypoint> destinations = getWaypoints(places);

        // Set other properties
        routesMatrixRequest.setOrigins(List.of(origin));
        routesMatrixRequest.setDestinations(destinations);
        routesMatrixRequest.setTravelMode("DRIVE");
        routesMatrixRequest.setRoutingPreference("TRAFFIC_AWARE");

        return routesMatrixRequest;
    }
    @NotNull
    private static List<RoutesMatrixRequest.Waypoint> getWaypoints(Location[] places) {
        List<RoutesMatrixRequest.Waypoint> destinations = new ArrayList<>();
        for(Location place : places) {
            RoutesMatrixRequest.Waypoint destination = new RoutesMatrixRequest.Waypoint();
            RoutesMatrixRequest.Waypoint.WaypointDetails destinationDetails = new RoutesMatrixRequest.Waypoint.WaypointDetails();
            destinationDetails.setPlaceId("ChIJZxSQSDrLpw0RQ---8sA65qU");
            destination.setWaypoint(destinationDetails);

            destinations.add(destination);
        }
        return destinations;
    }
    @NotNull
    private static RoutesMatrixRequest.Waypoint getWaypoint(Double lat, Double lon) {
        RoutesMatrixRequest.Waypoint origin = new RoutesMatrixRequest.Waypoint();
        RoutesMatrixRequest.Waypoint.WaypointDetails.wpLocation originLocation = new RoutesMatrixRequest.Waypoint.WaypointDetails.wpLocation();
        RoutesMatrixRequest.Waypoint.WaypointDetails.wpLocation.LatLng originLatLng = new RoutesMatrixRequest.Waypoint.WaypointDetails.wpLocation.LatLng();
        RoutesMatrixRequest.Waypoint.WaypointDetails waypointDetails = new RoutesMatrixRequest.Waypoint.WaypointDetails();
        originLatLng.setLatitude(lat);
        originLatLng.setLongitude(lon);
        originLocation.setLatLng(originLatLng);
        waypointDetails.setLocation(originLocation);
        origin.setWaypoint(waypointDetails);
        RoutesMatrixRequest.Waypoint.RouteModifiers originModifiers = new RoutesMatrixRequest.Waypoint.RouteModifiers();
        originModifiers.setAvoid_ferries(true);
        origin.setRouteModifiers(originModifiers);
        return origin;
    }


}

