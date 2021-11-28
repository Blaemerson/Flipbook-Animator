package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Flipbook;
import model.Thumbnail;

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
        @FXML
        private ImageView prevFrame;
        @FXML
        private ImageView nextFrame;
        @FXML
        private ScrollPane spTimeline;
        @FXML
        private HBox timelineBox;

    @FXML
        private Slider thickness;
        @FXML
        private Spinner animationSpeedSetter;

        private Thumbnail thumbnails;

        private Flipbook flipbook;
        private boolean onionSkinningOn = true;
        private String activeTool = "Pencil";

        // frame counter at bottom of application
        @FXML
        Label currentFrame;

        Stage myStage;

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

            firstFrame();
            flipbook.setFrame(0);
            setFrameCount();
            openFlipbook = true;

        }

        public void populateTimeline() {
            timelineBox.setSpacing(2);
            timelineBox.getChildren().clear();
            int index = 0;
            for (Image i : thumbnails.getThumbnails()) {
                ImageView thumb = new ImageView(i);
                thumb.setPreserveRatio(true);
                thumb.setFitHeight(84);
                timelineBox.getChildren().add(thumb);
                Tooltip.install(timelineBox.getChildren().get(index), new Tooltip("Frame " + (index + 1)));
                int finalIndex = index;
                timelineBox.getChildren().get(index).setOnMousePressed((MouseEvent e) -> {
                    addThumbnails(this.flipbook.getCurFrameNum());
                    seekTo(finalIndex);
                });
                index++;
            }
        }

        //TODO: seek to frames using mouseclick on thumbnails in timeline
        //public void seekTo

        //makes the first frame and allows other keyboard events to occur
        //TODO: Add new file menu, allow user to change canvas size at that time
        @FXML
        protected void newFile() {

            flipbook = new Flipbook(800, 640, "test");

            flipbookPane.setVisible(true);
            flipbookPane.setMaxSize(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas = new Canvas(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas.setOnMousePressed(e->{handleMousePressed(e);});

            canvas.setOnMouseDragged(e->{handleMouseDragged(e); });

            flipbookPane.getChildren().addAll(flipbook.getGroup(), canvas);

            pane.setVisible(true);
            //pane.setCenter(viewPort);


            layerPicker.setItems(FXCollections.observableArrayList("Layer 1", "Layer 2", "Layer 3"));
            layerPicker.setValue("Layer 1");
            flipbook.addFrame();
            setFrameCount();

            openFlipbook = true;
            thumbnails = new Thumbnail(this.flipbookPane);
        }


        //uses frameRate in flipbook to call the forward function at timed intervals
        public void animate() {

            populateTimeline();
            this.flipbook.setOnionSkinning(false);
            isAnimating = true;

            KeyFrame keyFrame = new KeyFrame(
                    //Duration.millis(Math.round(1.0/frameRate)*1000),
                    Duration.millis(flipbook.getFrameTime()),
                    event -> {
                        flipbook.forward(true);
                        updateThumbnails();
                        setFrameCount();
                        System.out.println("Frame #: " + flipbook.getCurFrameNum());
                    });

            Timeline timeline = new Timeline(keyFrame);

            timeline.setCycleCount(flipbook.getNumFrames() - flipbook.getCurFrameNum());

            timeline.play();

            timeline.setOnFinished(e -> {isAnimating = false;});

            setFrameCount();
        }


        //sets frame count in the bottom container
        public void setFrameCount() {
            currentFrame.setText((flipbook.getCurFrameNum()+1)+"");
        }

        public void handleMousePressed(MouseEvent e) {
            GraphicsContext gc = flipbook.getGraphicsContext(Character.getNumericValue(layerPicker.getValue().charAt(layerPicker.getValue().length()-1))-1);

            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
        }

        public void handleMouseDragged(MouseEvent e) {
            GraphicsContext gc = flipbook.getGraphicsContext(Character.getNumericValue(layerPicker.getValue().charAt(layerPicker.getValue().length()-1))-1);
            gc.setLineWidth(this.thickness.getValue());
            if (this.activeTool == "Eraser") {
                gc.clearRect(e.getX()-5, e.getY()-5, 10, 10);
            }
            else if (this.activeTool == "Pencil") {
                gc.setStroke(this.colorPicker.getValue());

                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
            else if (this.activeTool == "PaintBucket") {
                gc.setFill(this.colorPicker.getValue());
            }
        }

        public void updateThumbnails() {
            if (flipbook.getCurFrameNum() != 0) {
                this.prevFrame.setImage(thumbnails.getThumbnailAt(this.flipbook.getCurFrameNum()-1));
            }
            else {
                prevFrame.setImage(null);
            }
            if (flipbook.getCurFrameNum() != this.flipbook.getNumFrames()-1) {
                this.nextFrame.setImage(this.thumbnails.getThumbnailAt(this.flipbook.getCurFrameNum()+1));
            }
            else {
                nextFrame.setImage(null);
            }
            if (flipbook.getCurFrameNum() <= timelineBox.getChildren().size()-1) {
                timelineBox.getChildren().get(flipbook.getCurFrameNum()).setEffect(new DropShadow());
            }
        }

    @FXML
    protected void setPencil() {
            //flipbookPane.setCursor(new ImageCursor(new Image("resources/img/pen-solid.png"), 16, 16));
        this.activeTool = "Pencil";
    }
    @FXML
    protected void setPaintBucket() {
        flipbookPane.setCursor(Cursor.OPEN_HAND);
        this.activeTool = "PaintBucket";
    }
    @FXML
    protected void setEraser() {
        flipbookPane.setCursor(Cursor.CROSSHAIR);
        this.activeTool = "Eraser";
    }
    @FXML
    protected void setImage() {
        //opens a window to allow you to pick a .flip file
        FileChooser openImg = new FileChooser();
        openImg.setTitle("Open");
        openImg.getExtensionFilters().add(new ExtensionFilter("Image file", "*.png", "*.jpg"));
        File file = openImg.showOpenDialog(myStage);

        this.flipbook.getGraphicsContext(0).drawImage(new Image(file.toURI().toString()), 0, 0, this.flipbook.getCanvasWidth(), this.flipbook.getCanvasHeight()-2);
    }
    // File
    @FXML
    protected void onOpenFileChosen() {
        System.out.println("Open");
        open();
    }
    @FXML
    protected void onSaveFileChosen(ActionEvent event) {
        System.out.println("Save");
        save();
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
                this.flipbook.setFrame(0);
            }
            else {
                this.flipbook.deleteFrame(curFrame);
                this.flipbook.setFrame(curFrame-1);
            }
        this.thumbnails.remove(curFrame);
        populateTimeline();
        updateThumbnails();
        setFrameCount();
    }
    @FXML
    protected void onInsertFrame() {
        //this.flipbook.setOnionSkinning(false);
        this.flipbook.setOnionSkinning(false);
        this.thumbnails.insert(this.thumbnails.convert(this.flipbookPane), this.flipbook.getCurFrameNum());
        populateTimeline();
        this.flipbook.setOnionSkinning(onionSkinningOn);
        this.flipbook.addFrame();
        updateThumbnails();
        setFrameCount();
    }


    // View
    @FXML
    protected void toggleOnionSkinning() {
            this.onionSkinningOn = !this.onionSkinningOn;
            this.flipbook.setOnionSkinning(onionSkinningOn);
    }

    // Media Controls
    @FXML
    protected void play() {
        animate();
        this.flipbook.setOnionSkinning(onionSkinningOn);
    }
    @FXML
    protected void firstFrame() {
        addThumbnails(this.flipbook.getCurFrameNum());
        seekTo(0);
    }
    @FXML
    protected void lastFrame() {
        addThumbnails(this.flipbook.getCurFrameNum());
        seekTo(this.flipbook.getNumFrames() - 1);
    }
    @FXML
    protected void prevFrame() {
        /*
        this.flipbook.setOnionSkinning(false);
        this.thumbnails.insert(this.thumbnails.convert(this.flipbookPane), this.flipbook.getCurFrameNum());
        populateTimeline();
        this.flipbook.setOnionSkinning(onionSkinningOn);

        this.flipbook.backward();
        updateThumbnails();

        setFrameCount();
        */
        int curFrame = this.flipbook.getCurFrameNum();
        if (curFrame > 0) {
            addThumbnails(curFrame);
            seekTo(curFrame - 1);
        }
    }
    @FXML
    protected void nextFrame() {
        /*
        this.flipbook.setOnionSkinning(false);
        this.thumbnails.insert(this.thumbnails.convert(this.flipbookPane), this.flipbook.getCurFrameNum());
        populateTimeline();
        this.flipbook.setOnionSkinning(onionSkinningOn);
        if (this.flipbook.getCurFrameNum()==this.flipbook.getNumFrames()-1) {
            this.flipbook.addFrame();
        }
        this.flipbook.forward(false);
        updateThumbnails();

        System.out.println(this.flipbook.getCurFrameNum());

        setFrameCount();
         */

        int curFrame = this.flipbook.getCurFrameNum();
        addThumbnails(curFrame);
        if (curFrame == this.flipbook.getNumFrames() - 1) {
            this.flipbook.addFrame();
        }
        // Does the frame need to be saved?
        // this.flipbook.saveFrame();
        seekTo(curFrame + 1);
    }

    // TODO: if animating halt animation or make button unresponsive
    // not sure if saveframe() is needed, it was only in nextframe() and prevFrame()
    // via flipbook.forward() and flipbook.backward()
    protected void seekTo(int frameIndex) {
        /*
        this.flipbook.setOnionSkinning(false);
        this.thumbnails.insert(this.thumbnails.convert(this.flipbookPane), this.flipbook.getCurFrameNum());
        populateTimeline();
        this.flipbook.setOnionSkinning(onionSkinningOn);
         */
        this.flipbook.setFrame(frameIndex);
        updateThumbnails();
        setFrameCount();
    }

    // separated this from seekTo() to fix order of operations for nextFrame()
    protected void addThumbnails(int curFrame){
        this.flipbook.setOnionSkinning(false);
        this.thumbnails.insert(this.thumbnails.convert(this.flipbookPane), curFrame);
        populateTimeline();
        this.flipbook.setOnionSkinning(onionSkinningOn);
    }
}
