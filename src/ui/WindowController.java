package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
        // FXML objects; name corresponds to its FX ID
        @FXML
        private Canvas canvas;
        @FXML
        private BorderPane pane;
        @FXML
        private Pane flipbookPane;
        @FXML
        private ChoiceBox<String> layerPicker;
        @FXML
        private ColorPicker colorPicker;

    private Flipbook flipbook;


        // frame counter at bottom of application
        String frameMessage = "Current Frame: ";
        Label currentFrame = new Label(frameMessage + "--");

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

            //write data to .flip file
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

            flipbook.setFrame(0);
            setFrameCount(flipbook.getCurFrameNum());
            openFlipbook = true;

        }

        //makes the first frame and allows other keyboard events to occur
        //TODO: Add new file menu, allow user to change canvas size at that time
        @FXML
        protected void newFile() {

            flipbook = new Flipbook(600, 440, "test");

            flipbookPane.setMaxSize(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas = new Canvas(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas.setOnMousePressed(e->{handleMousePressed(e);});

            canvas.setOnMouseDragged(e->{handleMouseDragged(e); });

            flipbookPane.getChildren().addAll(flipbook.getGroup(), canvas);
            flipbookPane.setOpacity(1);

            pane.setCenter(flipbookPane);

            layerPicker.setItems(FXCollections.observableArrayList("Layer 1", "Layer 2", "Layer 3"));
            layerPicker.setValue("Layer 1");
            flipbook.addFrame();
            setFrameCount(flipbook.getCurFrameNum());

            openFlipbook = true;

        }


        //uses frameRate in flipbook to call the forward function at timed intervals
        public void animate() {

            isAnimating = true;

            KeyFrame keyFrame = new KeyFrame(
                    //Duration.millis(Math.round(1.0/frameRate)*1000),
                    Duration.millis(flipbook.getFrameTime()),
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
            GraphicsContext gc = flipbook.getGraphicsContext(Character.getNumericValue(layerPicker.getValue().charAt(layerPicker.getValue().length()-1))-1);

            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
        }

        public void handleMouseDragged(MouseEvent e) {
            GraphicsContext gc = flipbook.getGraphicsContext(Character.getNumericValue(layerPicker.getValue().charAt(layerPicker.getValue().length()-1))-1);
            if (e.isControlDown()) {
                gc.setLineWidth(5);
                gc.setStroke(Color.WHITE);
            }
            gc.setStroke(this.colorPicker.getValue());

            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        }

    // File
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
    protected void onDeleteChosen() {
            int curFrame = this.flipbook.getCurFrameNum();
            if (this.flipbook.getNumFrames() == 1) {
                this.flipbook.addFrame();
                this.flipbook.deleteFrame(curFrame);
                this.flipbook.setFrame(0);
            }
            else if (curFrame == 0) {
                this.flipbook.deleteFrame(curFrame);
                this.flipbook.setFrame(1);
            }
            else {
                this.flipbook.deleteFrame(curFrame);
                this.flipbook.setFrame(curFrame-1);
            }
    }
    @FXML
    protected void onInsertFrame() {
            this.flipbook.addFrame();
    }

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
            this.flipbook.update();
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
