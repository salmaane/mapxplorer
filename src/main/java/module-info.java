module com.example.mapxplorer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.mapxplorer to javafx.fxml;
    exports com.mapxplorer;
    exports com.mapxplorer.controller;
    opens com.mapxplorer.controller to javafx.fxml;
}