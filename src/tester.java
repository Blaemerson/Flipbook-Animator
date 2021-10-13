import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class tester extends Application {

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Flipbook flipbook = new Flipbook(640, 480, "test");
        
        Scene scene = new Scene(flipbook.getGroup(), 640, 480);
        flipbook.addFrame();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        
    }

}



//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//
//public class tester extends Application {
//
//    @Override
//    public void start(Stage stage) {
//        String javaVersion = System.getProperty("java.version");
//        String javafxVersion = System.getProperty("javafx.version");
//        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
//        Scene scene = new Scene(new StackPane(l), 640, 480);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }
//
//}