package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Flipbook;

import java.io.IOException;

public class Studio extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Studio.class.getResource("resources/window-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 480, 360);

        stage.setTitle("Onionskin Studio");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}