package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Flipbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class WindowController {
        @FXML
        private ScrollPane scrollpane;
        @FXML
        private Canvas canvas;
        @FXML
        private BorderPane pane;
        @FXML
        private Pane flipbookPane;

        private Flipbook flipbook;


        // frame counter at bottom of application
        String frameMessage = "Current Frame: ";
        Label currentFrame = new Label(frameMessage + "--");

        //content containers
        ToolBar toolbar;
        Stage myStage;
        HBox frameCountDisplay;
        

        //program name
        final String appTitle = "Flipbook Proto 2a";

        //prevents actions from occuring when there are potential conflicts
        boolean openFlipbook = false;
        boolean isAnimating = false;

        //top level save function, grabs string from flipbook save function
        public void save() {

            //creates string of variable states and encoded frames
            String fileForSave = flipbook.createFileForSave();


            //opens a window to allow the user to pick a file name
            //and destination
            FileChooser savefile = new FileChooser();
            savefile.setTitle("Save File");
            savefile.getExtensionFilters().add(new ExtensionFilter("Flip file", "*.flip"));

            //create a file in the destination they picked
            File file = savefile.showSaveDialog(myStage);

            //write data to .fap file
            if (file != null) {

                try {

                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(fileForSave);

                    //always close file streams
                    writer.close();
                }

                catch (IOException ex) {

                    System.out.println("Error opening file, or writing data.");
                }
            }

        }


        //takes previously stored data in .flip file and parses it back into a string
        public void open() {

            //opens a window to allow you to pick a .flip file
            FileChooser openfile = new FileChooser();
            openfile.setTitle("Open");
            openfile.getExtensionFilters().add(new ExtensionFilter("Flip file", "*.flip"));

            File file = openfile.showOpenDialog(myStage);

            newFile();

            flipbook.openFile(file);

            pane.setCenter(scrollpane);
            scrollpane.setContent(flipbookPane);
            flipbook.setFrame(0);
            setFrameCount(flipbook.getCurFrameNum());
            openFlipbook = true;

        }

        //makes the first frame and allows other keyboard events to occur
        //TODO: Add new file menu, allow user to change canvas size at that time
        @FXML
        protected void newFile() {

            flipbook = new Flipbook(400, 340, "test");

            flipbookPane.setMaxSize(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas = new Canvas(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas.setOnMousePressed(e->{handleMousePressed(e);});

            canvas.setOnMouseDragged(e->{handleMouseDragged(e); });

            flipbookPane.getChildren().addAll(flipbook.getGroup(), canvas);
            flipbookPane.setOpacity(1);

            scrollpane.setContent(flipbookPane);
            scrollpane.setFitToWidth(true);
            pane.setCenter(scrollpane);

            flipbook.addFrame();
            setFrameCount(flipbook.getCurFrameNum());

            openFlipbook = true;

        }


        //uses frameRate in flipbook to call the forward function at timed intervals
        public void animate() {

            isAnimating = true;

            KeyFrame keyFrame = new KeyFrame(Duration.millis(flipbook.getFrameTime()),
                    event -> {
                        flipbook.forward(true);
                        setFrameCount(flipbook.getCurFrameNum());
                        System.out.println("Frame #: " + flipbook.getCurFrameNum());
                    });

            Timeline timeline = new Timeline(keyFrame);

            timeline.setCycleCount(flipbook.getNumFrames() - flipbook.getCurFrameNum());

            timeline.play();

            timeline.setOnFinished(e -> {isAnimating = false;});
        }


        //sets frame count in the bottom container
        public void setFrameCount(int frameNumber) {
            currentFrame.setText(frameMessage + flipbook.getCurFrameNum());
        }


        public void handleMousePressed(MouseEvent e) {

            GraphicsContext gc = flipbook.getGraphicsContext();

            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());


        }

        public void handleMouseDragged(MouseEvent e) {

            GraphicsContext gc = flipbook.getGraphicsContext();

            gc.lineTo(e.getX(), e.getY());
            gc.stroke();

        }


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
    protected void toggleOnionSkinning() {
            this.flipbook.toggleOnionSkinning();
    }

    // Media Controls
    @FXML
    protected void play() {
            animate();
        }
    @FXML
    protected void firstFrame() {
            this.flipbook.setFrame(0);
            System.out.println(this.flipbook.getCurFrameNum());
    }
    @FXML
    protected void lastFrame() {
            this.flipbook.setFrame(this.flipbook.getNumFrames()-1);
            System.out.println(this.flipbook.getCurFrameNum());
    }
    @FXML
    protected void prevFrame() {
            this.flipbook.backward();
            System.out.println(this.flipbook.getCurFrameNum());
    }
    @FXML
    protected void nextFrame() {
        if (this.flipbook.getCurFrameNum()==this.flipbook.getNumFrames()-1) {
            this.flipbook.addFrame();
        }
        this.flipbook.forward(false);
        System.out.println(this.flipbook.getCurFrameNum());
    }
}