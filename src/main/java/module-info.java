module com.salmane.mapxplorer {
    requires javafx.controls;
    requires javafx.fxml;

    requires de.jensd.fx.glyphs.fontawesome;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires java.net.http;
    requires com.google.gson;
    requires java.dotenv;
    requires annotations;

    opens com.salmane.mapxplorer.model to com.google.gson;
    opens com.salmane.mapxplorer.request to com.google.gson;
    opens com.salmane.mapxplorer to javafx.fxml;
    exports com.salmane.mapxplorer;
    exports com.salmane.mapxplorer.controller;
    exports com.salmane.mapxplorer.model;
    opens com.salmane.mapxplorer.controller to javafx.fxml;
}