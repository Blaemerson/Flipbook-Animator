package com.onionskinstudio.flipbookanimator.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;

public class StartScreen extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartScreen.class.getResource("window-start.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 480, 360);

        stage.setTitle("Onionskin Studio");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
