package com.salmane.mapxplorer.controller;

import com.salmane.mapxplorer.model.DataManager;
import com.salmane.mapxplorer.model.Location;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class SidebarController {
    public AnchorPane sidebar;
    public FontAwesomeIconView homeIcon;
    public FontAwesomeIconView savedIcon;
    public FontAwesomeIconView offlineIcon;
    public FontAwesomeIconView aboutIcon;
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
    public HBox activateLocationMessage;
    @FXML
    public VBox nearbyPlacesContainer;
    @FXML
    public ListView<Location> placesListView;
    @FXML
    public ProgressIndicator spinner;
    Dotenv dotenv = Dotenv.load();


    public void initialize() {
        DataManager.getInstance().setSidebarController(this);
        DataManager.getInstance().setLatLonFields(latTextField, lonTextField);
        initTooltips();
        initTypesCombobox();
        initRadiusSlider();
    }

    private void initTooltips() {
        Tooltip homeTooltip = new Tooltip("Home");
        homeTooltip.setStyle("-fx-font-size: 10px;");
        homeTooltip.setShowDelay(Duration.millis(100));
        Tooltip savedTooltip = new Tooltip("Saved locations");
        savedTooltip.setStyle("-fx-font-size: 10px;");
        savedTooltip.setShowDelay(Duration.millis(100));
        Tooltip offlineTooltip = new Tooltip("Offline maps");
        offlineTooltip.setStyle("-fx-font-size: 10px;");
        offlineTooltip.setShowDelay(Duration.millis(100));
        Tooltip aboutTooltip = new Tooltip("About");
        aboutTooltip.setStyle("-fx-font-size: 10px;");
        aboutTooltip.setShowDelay(Duration.millis(100));
        Tooltip menuTooltip = new Tooltip("menu");
        menuTooltip.setStyle("-fx-font-size: 10px;");
        menuTooltip.setShowDelay(Duration.millis(100));

        Tooltip.install(homeIcon, homeTooltip);
        Tooltip.install(savedIcon, savedTooltip);
        Tooltip.install(offlineIcon, offlineTooltip);
        Tooltip.install(aboutIcon, aboutTooltip);
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
        placesListView.setDisable(false);
        nearbyPlacesContainer.getChildren().remove(activateLocationMessage);
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

        Task<Location[]> nearbyPLacesTask = getNearbyPlcaesTask();

        nearbyPLacesTask.setOnSucceeded(workerStateEvent -> {
            Location[] places = nearbyPLacesTask.getValue();
            if(!placesListView.getItems().isEmpty()) placesListView.getItems().clear();
            placesListView.getItems().addAll(places);
            searchButton.setText("Search");
            spinner.setVisible(false);
            searchButton.setDisable(false);
        });

        nearbyPLacesTask.setOnFailed(workerStateEvent -> {
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
                        dotenv.get("GOOGLE_API_KEY"), "*",
                        new String[]{String.join("_", selected)},
                        20, Double.valueOf(latTextField.getText()),
                        Double.valueOf(lonTextField.getText()),
                        radiusSlider.getValue()
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

}
