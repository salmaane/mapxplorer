package com.salmane.mapxplorer.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SidebarController {
    public AnchorPane sidebar;
    public FontAwesomeIconView homeIcon;
    public FontAwesomeIconView savedIcon;
    public FontAwesomeIconView offlineIcon;
    public FontAwesomeIconView aboutIcon;
    public FontAwesomeIconView menuIcon;
    private boolean collapsed = false;

    public void handleToggleMenu(ActionEvent event) {
        if(collapsed) {
            sidebar.setPrefWidth(230);
            sidebar.setClip(new Rectangle(240, 746));
            collapsed = false;
            return;
        }
        sidebar.setPrefWidth(43);
        sidebar.setClip(new Rectangle(45, 746));
        collapsed = true;
    }

    public void initialize() {
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
        Tooltip.install(menuIcon, menuTooltip);
    }
}
