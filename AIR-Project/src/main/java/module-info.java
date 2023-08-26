module com.example.airproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.fazecast.jSerialComm;
    requires org.apache.commons.io;
    requires java.desktop;
    requires org.json;

    opens com.vasll.airproject to javafx.fxml;
    exports com.vasll.airproject;
    exports com.vasll.airproject.Controllers;
    opens com.vasll.airproject.Controllers to javafx.fxml;
}