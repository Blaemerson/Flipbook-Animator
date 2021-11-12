package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WindowController {
    // File
    @FXML
    protected void onNewFileChosen() {
        System.out.println("New");
    }
    @FXML
    protected void onOpenFileChosen() {
        System.out.println("Open");
    }
    @FXML
    protected void onSaveFileChose(ActionEvent event) {
        System.out.println("Save");
    }

    // Edit
    @FXML
    protected void onDeleteChosen() { System.out.println("Delete Frame"); }
    // View
    @FXML
    protected void onToggleOnionSkinning() {
        System.out.println("Onion Skinning Toggle");
    }

    // Media Controls
    @FXML
    protected void play() { System.out.println("Play animation"); }
    @FXML
    protected void firstFrame() {
        System.out.println("First frame");
    }
    @FXML
    protected void lastFrame() {
        System.out.println("Last frame");
    }
    @FXML
    protected void prevFrame() {
        System.out.println("Previous frame");
    }
    @FXML
    protected void nextFrame() {
        System.out.println("Next frame");
    }
}