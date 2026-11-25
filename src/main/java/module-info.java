module com.example.malotiprime {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires jdk.httpserver;

    opens com.example.malotiprime.controller to javafx.fxml;
    exports com.example.malotiprime;
}