package com.example.malotiprime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.malotiprime.controller.MainController;

import java.io.IOException;

public class HelloApplication extends Application {
    private MainController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 720);
        controller = fxmlLoader.getController();
        scene.getStylesheets().add(
                HelloApplication.class.getResource("view/styles.css").toExternalForm());
        stage.setTitle("Maloti Prime");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        if (controller != null) {
            controller.shutdown();
        }
    }
}
