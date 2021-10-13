import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class tester extends Application {

    @Override
    public void start(Stage stage) {
    	
        Flipbook flipbook = new Flipbook(640, 480, "test");
        
        //the scene is what holds the group
        Scene scene = new Scene(flipbook.getGroup(), 640, 480);
        
        //adding a frame to the group to be displayed and drawn on
        flipbook.addFrame();
        
        //and the stage is the top level container; it's the actual window
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        
    }

}
