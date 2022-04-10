module JavaDrivesGTAV {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires jinput;
    requires javafx.swing;
    requires jnativehook;
    requires java.logging;

    opens com.peacefulotter.javadrivesgta to javafx.fxml;
    exports com.peacefulotter.javadrivesgta;
}