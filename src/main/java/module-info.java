module com.salmane.mapxplorer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires de.jensd.fx.glyphs.fontawesome;

    opens com.salmane.mapxplorer to javafx.fxml;
    exports com.salmane.mapxplorer;
    exports com.salmane.mapxplorer.controller;
    opens com.salmane.mapxplorer.controller to javafx.fxml;
}