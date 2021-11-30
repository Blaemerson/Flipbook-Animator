package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Flipbook;
import model.Thumbnail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
        private HBox timelineBox;
        @FXML
        private Slider thickness;

        @FXML
        private Spinner<Integer> fpsSetter;

        @FXML
        private MenuBar menuBarStartScreen;

        private Thumbnail thumbnails;

        private Flipbook flipbook;
        private boolean onionSkinningOn = true;
        private String activeTool = "Pencil";

        private boolean loaded = false;

        // frame counter at bottom of application
        @FXML
        Label currentFrame;

        Stage myStage;

    //program name
        final String appTitle = "Flipbook Proto 2a";

        //prevents actions from occuring when there are potential conflicts
        boolean openFlipbook = false;
        boolean isAnimating = false;

        // Displayed on new file menu chosen
        private static class NewFileBox {
            String newBookName;
            int newCanvasWidth;
            int newCanvasHeight;

            public NewFileBox(String title) {
                display(title);
            }

            public void display(String title) {
                Stage window = new Stage();

                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle(title);
                window.setMinWidth(250);
                Label label = new Label();
                label.setText("New File");
                TextField inputTitle = new TextField();
                inputTitle.setPromptText("Enter a title");
                //inputTitle.setMinWidth(80);
                inputTitle.setMaxWidth(80);
                Spinner<Integer> widths = new Spinner(100,800,800, 10);
                Spinner<Integer> heights = new Spinner(100,800,600, 10);

                widths.setEditable(true);
                heights.setEditable(true);

                Button confirmBtn = new Button("Confirm");
                confirmBtn.setDisable(true);
                inputTitle.setOnKeyTyped(keyEvent -> {
                    if (!inputTitle.getText().equals("")) {
                        confirmBtn.setDisable(false);
                    }
                    else {
                        confirmBtn.setDisable(true);
                    }
                });
                confirmBtn.setOnAction(event -> {
                    newBookName = inputTitle.getText();
                    newCanvasWidth = widths.getValue();
                    newCanvasHeight = heights.getValue();
                    window.close();
                });
                Button cancelBtn = new Button("Cancel");
                cancelBtn.setOnAction(event -> {
                    window.close();
                });

                VBox layout = new VBox(10);
                layout.getChildren().addAll(label, inputTitle, widths, heights, confirmBtn);
                layout.setAlignment(Pos.CENTER);
                Scene scene = new Scene(layout);
                window.setScene(scene);
                window.showAndWait();
            }

            public String getNewBookName() {
                return newBookName;
            }

            public int getNewCanvasWidth() {
                return newCanvasWidth;
            }

            public int getNewCanvasHeight() {
                return newCanvasHeight;
            }
        }


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

            //newFile();

            flipbook = new Flipbook(0, 0, "");
            flipbook.openFile(file);

            initEditor();

            flipbook.setFrame(0);

            // TODO: Don't use flipbookPane to set thumbnails. Preferably use flipbook.genFrameNodes()
            thumbnails = new Thumbnail(flipbookPane);
            for (int i=0; i<flipbook.getNumFrames(); i++) {
                flipbook.setFrame(i);
                addThumbnails(i);
            }

            //loadFileThumbnails();

            seekTo(0);

            //this is supposed to create thumbnails for a loaded file
            //the thumbnails show up as blank except the first one
            //loaded = true;

            setFrameCount();

            System.out.println("Num nodes in flipbook: " + flipbook.generateFrameNodes().size());

        }


        public void populateTimeline() {
            timelineBox.setSpacing(2);
            timelineBox.getChildren().clear();
            int index = 0;
            for (Image i : thumbnails.getThumbnails()) {
                prevFrame.setImage(i);
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

        //Switches scenes from startScreen when file > new is selected
        @FXML
        protected void newFileStartScreen() {
            // get a handle to the stage
            Stage stage = (Stage) menuBarStartScreen.getScene().getWindow();
            // do what you have to do
            stage.close();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Studio.class.getResource("resources/window-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 480, 360);
                stage = new Stage();
                stage.setScene(scene);
                stage.show();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        //makes the first frame and allows other keyboard events to occur
        @FXML
        protected void newFile() {
            //if(pane.isVisible()) {
            //    flipbook.addFrame();
                //flipbook.addFrame();
            //}
            timelineBox.getChildren().clear();

            prevFrame.setImage(null);
            nextFrame.setImage(null);

            NewFileBox nfb = new NewFileBox("New File");
            flipbook = new Flipbook(nfb.getNewCanvasWidth(), nfb.getNewCanvasHeight(), nfb.getNewBookName());
            flipbook.addFrame();
            initEditor();

            // TODO: Don't use flipbookPane to set thumbnails. Preferably use flipbook.genFrameNodes()
            thumbnails = new Thumbnail(flipbookPane);
            //seekTo(0);
        }

        // Do stuff that should happen regardless of whether new file or open file chosen
        public void initEditor() {
            flipbookPane.setVisible(true);
            flipbookPane.setMaxSize(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            flipbookPane.getChildren().clear();

            canvas = new Canvas(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas.setOnMousePressed(e->{handleMousePressed(e);});
            canvas.setOnMouseDragged(e->{handleMouseDragged(e); });
            canvas.setOnMouseReleased(e->{handleMouseReleased(e);});

            flipbookPane.getChildren().addAll(flipbook.getGroup(), canvas);

            pane.setVisible(true);

            layerPicker.setItems(FXCollections.observableArrayList("Layer 1", "Layer 2", "Layer 3"));
            layerPicker.setValue("Layer 1");
            flipbook.clearScreen();
            flipbook.update();

            openFlipbook = true;

            fpsSetter.setPromptText("FPS");
            //    addThumbnails(0);
        }



        //uses frameRate in flipbook to call the forward function at timed intervals
        public void animate() {

            //populateTimeline();
            this.fpsSetter.commitValue();
            this.flipbook.setFrameRate(this.fpsSetter.getValue());
            this.flipbook.setOnionSkinning(false);
            isAnimating = true;

            KeyFrame keyFrame = new KeyFrame(
                    //Duration.millis(Math.round(1.0/this.fpsSetter.getValue())*1000),
                    Duration.millis(flipbook.getFrameTime()),
                    event -> {
                        flipbook.forward(true);
                        populateTimeline();
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

            if (this.activeTool == "Eyedropper") {
                //floodFill(e.getX(), e.getY(), this.colorPicker.getValue(), gc);
                this.colorPicker.setValue(thumbnails.getThumbnailAt(this.flipbook.getCurFrameNum()).getPixelReader().getColor((int)e.getX(), (int)e.getY()));
            }
        }

        public void handleMouseReleased(MouseEvent e) {
            flipbook.saveFrame();
            addThumbnails(this.flipbook.getCurFrameNum());
            updateThumbnails();
            System.out.println(flipbook.getCurFrameNum());
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
        }

        public void updateThumbnails() {
            System.out.println("Number of Frames updateThumbnails(): " + flipbook.getFrames().size());
            int curFrameNum = this.flipbook.getCurFrameNum();
            /*
            //this is supposed to create thumbnails for a loaded file
            //the thumbnails show up as blank except the first one
            if(loaded){
                //loadFileThumbnails();
                loaded = false;
            }
             */
            if (flipbook.getCurFrameNum() != 0) {
                //pane.setLeft(prevFrame);
                System.out.println(thumbnails.getThumbnailAt(curFrameNum-1).toString());
                this.prevFrame.setImage(thumbnails.getThumbnailAt(curFrameNum-1));
                prevFrame.setVisible(true);
            }
            else {
                prevFrame.setVisible(false);
            }
            if (flipbook.getCurFrameNum() != this.flipbook.getNumFrames()-1) {
            	System.out.println(flipbook.getCurFrameNum() + ":" + flipbook.getNumFrames());
                this.nextFrame.setImage(thumbnails.getThumbnailAt(curFrameNum+1));
                nextFrame.setVisible(true);
            }
            else {
                nextFrame.setVisible(false);
            }
            if (flipbook.getCurFrameNum() <= timelineBox.getChildren().size()-1) {
                timelineBox.getChildren().get(curFrameNum).setEffect(new DropShadow());
            }
            prevFrame.setFitWidth(flipbook.getCanvasWidth()*.75);
            nextFrame.setFitWidth(flipbook.getCanvasWidth()*.75);
        }

    @FXML
    protected void setPencil() {
            //flipbookPane.setCursor(new ImageCursor(new Image("resources/img/pen-solid.png"), 16, 16));
        this.activeTool = "Pencil";
    }
    @FXML
    protected void setPaintBucket() {
        //flipbookPane.setCursor(Cursor.OPEN_HAND);
        this.activeTool = "Eyedropper";
    }
    @FXML
    protected void setEraser() {
        //flipbookPane.setCursor(Cursor.CROSSHAIR);
        this.activeTool = "Eraser";
    }
    @FXML
    protected void setImage() {
        //opens a window to allow you to pick a .flip file
        FileChooser openImg = new FileChooser();
        openImg.setTitle("Open");
        openImg.getExtensionFilters().add(new ExtensionFilter("Image file", "*.png", "*.jpg"));
        File file = openImg.showOpenDialog(myStage);

        this.flipbook.getGraphicsContext(0).drawImage(new Image(file.toURI().toString()), 0, 0, this.flipbook.getCanvasWidth(), this.flipbook.getCanvasHeight());
        addThumbnails(this.flipbook.getCurFrameNum());
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
        this.flipbook.addFrame();
        thumbnails.shiftThumbnails(this.flipbook.getCurFrameNum());
        addThumbnails(this.flipbook.getCurFrameNum());
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
        this.flipbook.setOnionSkinning(false);
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
        int curFrame = this.flipbook.getCurFrameNum();
        if (curFrame > 0) {
            addThumbnails(curFrame);
            seekTo(curFrame - 1);
        }
    }
    @FXML
    protected void nextFrame() {
        int curFrame = this.flipbook.getCurFrameNum();
        if (curFrame == this.flipbook.getNumFrames() - 1) {
            this.flipbook.addFrame();
            addThumbnails(curFrame+1);
            seekTo(curFrame+1);
        }
        else{
            timelineBox.getChildren().get(curFrame).setEffect(null);
            seekTo(curFrame+1);
        }
        //seekTo(curFrame + 1);
    }

    // TODO: if animating halt animation or make button unresponsive
    // not sure if saveframe() is needed, it was only in nextframe() and prevFrame()
    // via flipbook.forward() and flipbook.backward()
    protected void seekTo(int frameIndex) {
        flipbook.saveFrame();
        this.flipbook.setFrame(frameIndex);
        updateThumbnails();
        setFrameCount();
    }

    // separated this from seekTo() to fix order of operations for nextFrame()
    protected void addThumbnails(int curFrame){
        this.flipbook.setOnionSkinning(false);
        // TODO: Don't use flipbookPane to set thumbnails. Preferably use flipbook.genFrameNodes()
        this.thumbnails.insert(thumbnails.convert(flipbookPane), curFrame);
        populateTimeline();
        this.flipbook.setOnionSkinning(onionSkinningOn);
    }

    // TODO: Use this instead of the 'for' loop in open()
    protected void loadFileThumbnails() {
        int curFrame = 0;
        List<Node> temp = flipbook.generateFrameNodes();
        for (Node f : flipbook.generateFrameNodes()) {
            curFrame++;
            thumbnails.insert(thumbnails.convert(f), curFrame);
        }
    }
}
