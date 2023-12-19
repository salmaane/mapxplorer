package com.salmane.mapxplorer.controller;

import com.salmane.mapxplorer.model.DataManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

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
    ValidationSupport validationSupport = new ValidationSupport();


    public void initialize() {
        DataManager.getInstance().setSidebarController(this);
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
        validationSupport.registerValidator(typesComboBox, Validator.createEmptyValidator("A type place is required"));
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
    }

}
