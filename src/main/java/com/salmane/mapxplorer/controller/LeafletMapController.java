package com.salmane.mapxplorer.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.salmane.mapxplorer.model.DataManager;
import com.salmane.mapxplorer.model.Location;
import com.salmane.mapxplorer.model.PlacePredictions;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.controlsfx.control.Rating;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LeafletMapController {
    @FXML
    public WebView webview;
    public WebEngine engine;
    @FXML
    public TextField searchbar;
    @FXML
    public ListView<PlacePredictions> autocompleteList;
    @FXML
    public FontAwesomeIconView closeIcon;
    @FXML
    public FontAwesomeIconView myLocationIcon;
    @FXML
    public Tooltip myLocationTooltip;
    @FXML
    public FontAwesomeIconView returnToLocationIcon;
    @FXML
    public VBox placeInfoBox;
    @FXML
    public ScrollPane placeInfoScrollPane;
    public TranslateTransition detailsBoxTransitionUp;
    public TranslateTransition detailsBoxTransitionDown;
    private Location activeLocation = null;
    private Location myLocation = null;
    private LocationController locationController;
    private final Gson gson = new Gson();
    private Timer timer = new Timer();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    String googleApiKey = Dotenv.load().get("GOOGLE_API_KEY");
    private ArrayList<PlacePredictions> possibleSuggestions;
    private static LeafletMapController instance;
    private final Map<String, ImageView> imageCache = new HashMap<>();

    public void initialize() {
        engine = webview.getEngine();
        webview.setCache(true);
        engine.load(String.valueOf(getClass().getResource("/com/salmane/mapxplorer/javascript/index.html")));

        locationController = new LocationController(engine);
        DataManager.getInstance().setLocationController(locationController);
        DataManager.getInstance().setEngine(engine);
        DataManager.getInstance().setLeafletMapController(this);

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                instance = this; 
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("LeafletMapController", instance);
            }
        });

        initSearchbar();
        initPLaceInfoScrollPane();
    }

    public void handleMarkerClick(String location) {
        try {
            Location place = gson.fromJson(location, Location.class);
            preparePlaceInfoBox(place);

            Task<List<ImageView>> imagesTask = loadImagesTask(place.getPhotos(), 200, 300);
            imagesTask.setOnSucceeded(workerStateEvent -> {
                    HBox hbox = new HBox(new Text("Photos"));
                    Separator sep = new Separator();
                    FlowPane photosGrid = new FlowPane();
                    photosGrid.getStyleClass().add("photos-grid");
                    photosGrid.getChildren().addAll(imagesTask.getValue());

                    VBox photosContainer = new VBox(hbox, sep, photosGrid);
                    photosContainer.getStyleClass().add("photos-container");
                    photosContainer.setSpacing(5);

                    placeInfoBox.getChildren().add(photosContainer);
            });
            Thread thread = new Thread(imagesTask);
            thread.start();

            detailsBoxTransitionUp.play();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void preparePlaceInfoBox(Location place) {
        placeInfoBox.getChildren().clear();

        FontAwesomeIconView closeInfoBox = new FontAwesomeIconView();
        closeInfoBox.setGlyphName("ARROW_LEFT");
        closeInfoBox.setGlyphSize(22);
        closeInfoBox.getStyleClass().add("close-info-box");
        closeInfoBox.setOnMouseClicked((event) -> detailsBoxTransitionDown.play());
        HBox closeIconContainer = new HBox(closeInfoBox);
        closeIconContainer.setAlignment(Pos.CENTER_LEFT);
        closeIconContainer.setPadding(new Insets(4, 0, 0, 0));
        placeInfoBox.getChildren().add(closeIconContainer);

        VBox titleContainer = new VBox();
        titleContainer.setSpacing(3);
        if(place.getDisplayName() != null) {
            Text displayName = new Text(place.getDisplayName().getText());
            displayName.getStyleClass().add("display-name");
            titleContainer.getChildren().add(displayName);
        }
        if(place.getPrimaryTypeDisplayName() != null) {
            Text typeDisplayName = new Text(place.getPrimaryTypeDisplayName().getText());
            typeDisplayName.getStyleClass().add("type-display-name");
            titleContainer.getChildren().add(typeDisplayName);
        }
        if(place.getRating() != null) {
            Rating rating = new Rating();
            rating.getStyleClass().add("rating");
            rating.setPartialRating(true);
            rating.setUpdateOnHover(false);
            rating.setRating(place.getRating());
            Text ratingCount = new Text("(" + place.getRating() + ")");
            ratingCount.getStyleClass().add("rating-count");
            HBox ratingContainer = new HBox(rating, ratingCount);
            ratingContainer.setSpacing(10);
            titleContainer.getChildren().add(ratingContainer);
        }
        placeInfoBox.getChildren().add(titleContainer);


        VBox detailsContainer = new VBox();
        detailsContainer.setSpacing(10);
        if(place.getNationalPhoneNumber() != null) {
            FontAwesomeIconView phoneIcon = new FontAwesomeIconView();
            phoneIcon.getStyleClass().add("details-icon");
            phoneIcon.setGlyphName("PHONE");
            phoneIcon.setGlyphSize(20);
            Text phoneNumber = new Text(place.getNationalPhoneNumber());
            phoneNumber.getStyleClass().add("details-text");
            HBox phoneContainer = new HBox(phoneIcon, phoneNumber);
            phoneContainer.setSpacing(10);
            detailsContainer.getChildren().add(phoneContainer);
        }
        if(place.getFormattedAddress() != null) {
            FontAwesomeIconView addressIcon = new FontAwesomeIconView();
            addressIcon.getStyleClass().add("details-icon");
            addressIcon.setGlyphName("MAP_PIN");
            addressIcon.setGlyphSize(20);
            Text address = new Text(place.getFormattedAddress());
            address.getStyleClass().add("details-text");
            HBox addressContainer = new HBox(addressIcon, address);
            addressContainer.setSpacing(15);
            detailsContainer.getChildren().add(addressContainer);
        }
        if(place.getWebsiteUri() != null) {
            FontAwesomeIconView websiteIcon = new FontAwesomeIconView();
            websiteIcon.getStyleClass().add("details-icon");
            websiteIcon.setGlyphName("LINK");
            websiteIcon.setGlyphSize(20);
            Text website = new Text(place.getWebsiteUri());
            website.getStyleClass().add("details-text");
            HBox websiteContainer = new HBox(websiteIcon, website);
            websiteContainer.setSpacing(10);
            detailsContainer.getChildren().add(websiteContainer);
        }
        placeInfoBox.getChildren().add(detailsContainer);

        if(place.getTypes() != null) {
            HBox typesContainer = new HBox();
            typesContainer.getStyleClass().add("types-container");
            for (String type : place.getTypes()) {
                Button button = new Button();
                button.setDisable(true);
                button.setText(String.join(" ", type.split("_")));
                typesContainer.getChildren().add(button);
            }
            placeInfoBox.getChildren().add(typesContainer);
        }
    }
    private void initPLaceInfoScrollPane() {
        placeInfoScrollPane.setMaxWidth(640);
        placeInfoScrollPane.setTranslateY(780);

        detailsBoxTransitionUp = new TranslateTransition(Duration.millis(400), placeInfoScrollPane);
        detailsBoxTransitionUp.setFromY(780);
        detailsBoxTransitionUp.setToY(0);

        detailsBoxTransitionDown = new TranslateTransition(Duration.millis(400), placeInfoScrollPane);
        detailsBoxTransitionDown.setFromY(0);
        detailsBoxTransitionDown.setToY(780);

        placeInfoScrollPane.setContent(placeInfoBox);
    }
    private String buildImageUrl(String photoReference, String apiKey, Integer maxWidth, Integer maxHeight) {
        String endpoint = "https://places.googleapis.com/v1/%s/media?key=%s&maxWidthPx=%d&maxHeightPx=%d";
        return String.format(endpoint, photoReference, apiKey, maxWidth, maxHeight);
    }
    private Task<List<ImageView>> loadImagesTask(Location.Photo[] photos, Integer imageWidth, Integer imageHeight) {
        return new Task<List<ImageView>>() {
            @Override
            protected List<ImageView> call() throws Exception {
                List<ImageView> imagesList = new ArrayList<>();
                for (Location.Photo photo : photos) {
                    String imageUrl = buildImageUrl(photo.getName(), googleApiKey, imageWidth, imageHeight);
                    if(imageCache.containsKey(imageUrl)) {
                        imagesList.add(imageCache.get(imageUrl));
                    } else {
                        ImageView imageView = new ImageView(imageUrl);
                        imageCache.put(imageUrl, imageView);
                        imagesList.add(imageView);
                    }
                }
                System.out.println(imagesList.size());
                return imagesList;
            }
        };
    }

    private void initSearchbar() {
        searchbar.setOnKeyReleased(this::handleSearchEvent);

        PauseTransition delay = new PauseTransition(Duration.seconds(0.2));
        delay.setOnFinished(e -> autocompleteList.setVisible(false));
        searchbar.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (activeLocation != null) searchbar.setText(activeLocation.getFormattedAddress());
                delay.playFromStart();
            } else {
                handleSearchEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UNDEFINED, false, false, false, false));
            }
        });

        autocompleteList.setOnMouseClicked(this::handleAutocompleteSelection);

        closeIcon.setOnMouseClicked(event -> {
            activeLocation = locationController.removeLocationMarker(
                    event, searchbar,
                    closeIcon,
                    returnToLocationIcon,
                    autocompleteList,
                    activeLocation
            );
            detailsBoxTransitionDown.play();
        });
        returnToLocationIcon.setOnMouseClicked(event -> locationController.GoToLocation(activeLocation));

        myLocationTooltip.setShowDelay(new Duration(100));
        myLocationTooltip.setShowDuration(new Duration(900));
        myLocationIcon.setOnMouseClicked(event -> locationController.goToDeviceLocation(event, myLocation));
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

                HttpResponse<String> response = null;

                try {
                    HttpRequest getRequest = HttpRequest.newBuilder()
                            .uri(new URI(
                                    "https://maps.googleapis.com/maps/api/place/autocomplete/json?"
                                            + "key=" + googleApiKey
                                            + "&input=" + searchbar.getText().replaceAll("\\s", "+")
                            )).GET()
                            .build();

                    response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                try {
                    JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                    JsonArray results = jsonObject.get("predictions").getAsJsonArray();
                    Type placePredictionType = new TypeToken<ArrayList<PlacePredictions>>() {
                    }.getType();
                    possibleSuggestions = gson.fromJson(results, placePredictionType);
                } catch (Exception e) {
                    possibleSuggestions = new ArrayList<>();
                }

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
    private void handleAutocompleteSelection(MouseEvent event) {
        PlacePredictions selectedPrediction = autocompleteList.getSelectionModel().getSelectedItem();
        if (selectedPrediction != null) {
            autocompleteList.setVisible(false);

            CompletableFuture.supplyAsync(() -> {
                try {
                    String fields = "*";
                    HttpRequest getRequest = HttpRequest.newBuilder()
                            .uri(new URI(
                                    "https://places.googleapis.com/v1/places/"
                                            + selectedPrediction.getPlace_id() + "?"
                                            + "key=" + googleApiKey
                                            + "&fields=" + fields
                            )).GET()
                            .build();

                    HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                    return response.body();
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }).thenAcceptAsync(body -> {
                searchbar.setText(selectedPrediction.toString());
                activeLocation = gson.fromJson(body, Location.class);
                closeIcon.setVisible(true);
                returnToLocationIcon.setVisible(true);
                locationController.GoToLocation(activeLocation);
            }, Platform::runLater);
        }
    }

}