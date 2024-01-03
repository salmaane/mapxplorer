package com.salmane.mapxplorer.controller;

import com.google.gson.Gson;
import com.salmane.mapxplorer.model.DataManager;
import com.salmane.mapxplorer.model.Location;
import com.salmane.mapxplorer.model.Route;
import com.salmane.mapxplorer.model.TablePlaceInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import org.jetbrains.annotations.NotNull;

public class SidebarController {
    public AnchorPane sidebar;
    @FXML
    public ComboBox<String> typesComboBox;
    @FXML
    public Slider radiusSlider;
    @FXML
    public Label radiusLabel;
    @FXML
    public TextField latTextField;
    @FXML
    public TextField lonTextField;
    @FXML
    public Button searchButton;
    @FXML
    public Text placesNumber;
    @FXML
    public HBox activateLocationMessage;
    @FXML
    public VBox nearbyPlacesContainer;
    @FXML
    public ProgressIndicator spinner;
    @FXML
    public Button clearPLacesButton;
    private final String googleApiKey = DataManager.GOOGLE_API_KEY;
    private final Gson gson = new Gson();
    @FXML
    public TableView<TablePlaceInfo> placesTable;
    @FXML
    public TableColumn<TablePlaceInfo, String> nameCol;
    @FXML
    public TableColumn<TablePlaceInfo, String> distanceCol;

    public void initialize() {
        DataManager.getInstance().setSidebarController(this);
        DataManager.getInstance().setLatLonFields(latTextField, lonTextField);
        initTypesCombobox();
        initRadiusSlider();
        initPLacesTableView();
    }

    private void initTypesCombobox() {
        String[] placeTypes = new String[]{
                "Restaurant","Police", "Hospital", "Pharmacy", "Doctor", "Hotel", "Mosque", "Market",
                "Store", "Supermarket", "Shopping mall", "Gym", "Stadium", "Sports club", "Airport",
                "Train station"
        };
        typesComboBox.getItems().addAll(placeTypes);
    }
    private void initRadiusSlider() {
        radiusSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            radiusLabel.setText(String.format("Choose circle radius - %.0f km", newValue));
        });
    }
    public void enableControls() {
        typesComboBox.setDisable(false);
        radiusSlider.setDisable(false);
        searchButton.setDisable(false);
        placesTable.setDisable(false);
        nearbyPlacesContainer.getChildren().remove(activateLocationMessage);
    }
    public void initPLacesTableView() {
        nameCol.setResizable(false);
        distanceCol.setResizable(false);
        nameCol.setCellValueFactory(new PropertyValueFactory<TablePlaceInfo, String>("Name"));
        distanceCol.setCellValueFactory(new PropertyValueFactory<TablePlaceInfo, String>("Distance"));
        nameCol.setSortable(false);
        distanceCol.setSortable(false);
    }

    @FXML
    private void handleSearchButtonClick(ActionEvent event) {
        if(typesComboBox.getSelectionModel().isEmpty()) {
            showAlert("Place type", "Please select a place type to search for");
            return;
        }

        searchButton.setText("");
        spinner.setVisible(true);
        searchButton.setDisable(true);
        if(DataManager.getInstance().getLeafletMapController().placeInfoScrollPane.getTranslateY() == 0) {
            DataManager.getInstance().getLeafletMapController().detailsBoxTransitionDown.play();
        }

        Task<Location[]> nearbyPLacesTask = getNearbyPlcaesTask();

        nearbyPLacesTask.setOnSucceeded(workerStateEvent -> {
            WebEngine engine = DataManager.getInstance().getEngine();
            Location[] places = nearbyPLacesTask.getValue();

            Task<Route[]> getRoutesTask = getRoutesTask(
                    Double.valueOf(latTextField.getText()),
                    Double.valueOf(lonTextField.getText()),
                    places
            );
            getRoutesTask.setOnSucceeded(workerStateEvent1 -> {
                Route[] routes = getRoutesTask.getValue();

                ObservableList<TablePlaceInfo> placeInfoList = placesTable.getItems();
                placeInfoList.clear();
                for(Route route : routes) {
                    int destIndex = route.getDestinationIndex();
                    Location place = places[destIndex];
                    place.setRoute(route);
                    String name = place.getDisplayName().getText();
                    String distance = route.getDistanceMeters() + " m";
                    if (route.getDistanceMeters() > 1000) {
                        distance = String.format("%.1f",(double)route.getDistanceMeters()/1000) + " Km";
                    }
                    placeInfoList.add(new TablePlaceInfo(name, distance, place, route));
                }
                placesTable.setItems(placeInfoList);

                engine.executeScript("placeMarkers("
                        + gson.toJson(places)
                        + "," + gson.toJson(DataManager.getInstance().getMyLocation())
                        + "," + radiusSlider.getValue() * 1_000
                +")");

                // UI UPDATES
                placesNumber.setText("(" +  places.length + ")");
                searchButton.setText("Search");
                spinner.setVisible(false);
                searchButton.setDisable(false);
                clearPLacesButton.setVisible(true);
            });
            getRoutesTask.setOnFailed(workerStateEvent1 -> {
                // UI UPDATES
                searchButton.setText("Search");
                spinner.setVisible(true);
                searchButton.setDisable(false);

                Throwable exception = getRoutesTask.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            });
            new Thread(getRoutesTask).start();

        });
        nearbyPLacesTask.setOnFailed(workerStateEvent -> {
            // UI UPDATES
            searchButton.setText("Search");
            spinner.setVisible(true);
            searchButton.setDisable(false);
        });
        new Thread(nearbyPLacesTask).start();
    }
    @NotNull
    private Task<Location[]> getNearbyPlcaesTask() {
        String[] selected = typesComboBox.getSelectionModel().getSelectedItem()
                .toLowerCase().split(" ");

        return new Task<Location[]>() {
            @Override
            protected Location[] call() throws Exception {
                return DataManager.getInstance().getLocationController().getNearbyPlaces(
                        googleApiKey, "*",
                        new String[]{String.join("_", selected)},
                        20, Double.valueOf(latTextField.getText()),
                        Double.valueOf(lonTextField.getText()),
                        radiusSlider.getValue()
                );
            }
        };
    }
    private Task<Route[]> getRoutesTask(Double lat, Double lon, Location[] places) {
        return new Task<Route[]>() {
            @Override
            protected Route[] call() throws Exception {
                return DataManager.getInstance().getLocationController().getRoutes(
                        googleApiKey,
                        "originIndex,destinationIndex,status,condition,distanceMeters,duration",
                        lat, lon, places
                );
            }
        };
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    public void handleClearPLaces() {
            placesTable.getItems().clear();
            DataManager.getInstance().getEngine().executeScript("removePLacesMarkers()");
            typesComboBox.valueProperty().setValue(null);
            radiusSlider.setValue(1);
            clearPLacesButton.setVisible(false);
            if(DataManager.getInstance().getLeafletMapController().placeInfoScrollPane.getTranslateY() == 0) {
                DataManager.getInstance().getLeafletMapController().detailsBoxTransitionDown.play();
            }
    }

}
