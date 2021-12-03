package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.*;
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
import javafx.scene.transform.Transform;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Flipbook;
import model.SQLite;
import model.Thumbnail;
import model.Thumbnails;

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
        private ImageView prevFrameImg;
        @FXML
        private ImageView nextFrameImg;
        @FXML
        private ScrollPane spTimeline;
        @FXML
        private HBox timelineBox;
        @FXML
        private Slider pencilWidthSlider;

        @FXML
        private Spinner<Integer> fpsSetter;

        @FXML
        private MenuBar menuBarStartScreen;

        @FXML
        private ImageView playBtnIcon;

        private Thumbnails thumbnails;

        private Flipbook flipbook;
        private boolean onionSkinningOn = true;

        // There is probably a better way to implement tool switching
        private String activeTool;

        // TODO: keep as many fxml objects in the fxml file as possible
        @FXML
        private Pane deleteAndInsertSpacer;

        private boolean loaded = false;

        // frame counter at bottom of application
        @FXML
        Label frameNumLabel;
        @FXML
        private Pane toolsPane;
        @FXML
        private Pane mediaPane;

    Stage myStage;

    //program name
        final String appTitle = "Onionskin Studio";

        //prevents actions from occuring when there are potential conflicts
        boolean openFlipbook = false;
        boolean isAnimating = false;

        // Displayed on new file menu chosen
        // TODO: turn this into fxml file and load it
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
                    //SQLite.connect();
                    //SQLite.createNewTable();
                    SQLite.insert(flipbook.getBookName(), flipbook.getFrameImgString(0), file.getPath());
                } catch (IOException ex) {

                    System.out.println("Error opening file, or writing data.");
                }
            }

        } // end save()


        //takes previously stored data in .flip file and parses it back into a string
        public void open() {

            //opens a window to allow you to pick a .flip file
            FileChooser openfile = new FileChooser();
            openfile.setTitle("Open");
            openfile.getExtensionFilters().add(new ExtensionFilter("Flip file", "*.flip"));

            File file = openfile.showOpenDialog(myStage);

            flipbook = new Flipbook(0,0,"");
            flipbook.openFile(file);

            //newFile();
            //passing a true so that the newFile function knows
            //that it should not add a frame to the canvas, only open the flipbook
            newFile(true);


            
            //after opening the file, the frames should already be made, and the thumbnails can be made
            thumbnails = new Thumbnails(flipbook.generateFrameNodes());
            
            
            //populate the timeline with the newly made thumbnails
            populateTimeline();
            
            //not sure why this is called. The first frame is 'seeked' to and then addThumbnail is called
            firstFrame();
                              
            populateTimeline();
            openFlipbook = true;
            
        }

        public void populateTimeline() {
        	  	
        	int width = (int)((84.0/640)*800);
        	
        	//set spacing between thumnails and clear the timeline
            timelineBox.setSpacing(2);
            timelineBox.getChildren().clear();
            
            //why use an enhanced for loop if you need to use an index?
            int index = 0;
            for (Thumbnail t : thumbnails.getThumbnails()) {
                ImageView thumb = new ImageView(t.getThumbnailImage());
                
                //setting height of thumbnail to fit timeline box and prevent stretching
                thumb.setPreserveRatio(true);
                thumb.setFitHeight(84);
                
                Transform transform = thumb.getLocalToParentTransform();
                transform.setOnTransformChanged(e-> {
                	
                	System.out.println("event called");
                	
                	if(transform.getTx() > 200) {
                		thumb.setVisible(false);
                	}
                	
                	
                });
                
                
                //add thumbnail to the box
                timelineBox.getChildren().add(thumb);
                
                
                
                //adding frame to hover over tooltip
                Tooltip.install(timelineBox.getChildren().get(index), new Tooltip("Frame " + (index + 1)));
                
                //what is final index for?
                int finalIndex = index;
                timelineBox.getChildren().get(index).setOnMousePressed((MouseEvent e) -> {
                	//why is add thumbnails being called when the thumbnail is pressed?
                	//why not only seek to it?
                    addThumbnails(this.flipbook.getCurFrameNum());
                    //why not just use index if final index is equal to index?
                    seekTo(finalIndex);
                    
                });
                
                index++;
                
                if(index > 20)
                	break;
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

        @FXML
        
        protected void _newFile() {
        	newFile(false);
        }
        

        @FXML
        protected void newFile(boolean fromOpen) {

        	flipbookPane.getChildren().clear();

            if (!fromOpen) {
                NewFileBox nfb = new NewFileBox("New File");
                flipbook = new Flipbook(nfb.getNewCanvasWidth(), nfb.getNewCanvasHeight(), nfb.getNewBookName());
            }

            toolsPane.setDisable(false);
            mediaPane.setDisable(false);

            flipbookPane.setVisible(true);
            flipbookPane.setMaxSize(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            canvas = new Canvas(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());

            //setting behavior for draw canvas
            //this canvas 
            canvas.setOnMousePressed(this::handleMousePressed);
            canvas.setOnMouseDragged(this::handleMouseDragged);
            canvas.setOnMouseReleased(this::handleMouseReleased);

            flipbookPane.getChildren().addAll(flipbook.getGroup(), canvas);

            pane.setVisible(true);

            // Aligning trash and insert icons above canvas
            deleteAndInsertSpacer.setMinWidth(canvas.getWidth()-32);

            // Scaling thumbnails so their smaller than the active frame
            prevFrameImg.setFitWidth(canvas.getWidth()*.7);
            nextFrameImg.setFitWidth(canvas.getWidth()*.7);


            //set up layer picker
            layerPicker.setItems(FXCollections.observableArrayList("Layer 1", "Layer 2", "Layer 3"));
            layerPicker.setValue("Layer 1");
            
            //add the single frame to the new file
            //if we're opening a file we don't want a random frame already there
            if(!fromOpen) {

                flipbook.addFrame();
                setFrameCount();

                //in new file, there is only one frame to add to the thumbnails list.
                //just call Thumbnails(flipbook.generateFrameNodes())
                //additionally, when you make a new file you need to repopulate the timeline
                thumbnails = new Thumbnails(flipbook.generateFrameNodes());
                populateTimeline();
      
            }

            setPencil();
            openFlipbook = true;
            
            
        }


        //uses frameRate in flipbook to call the forward function at timed intervals
        public void animate() {
            isAnimating = true;

            //populateTimeline();
            this.flipbook.setOnionSkinning(false);
            this.fpsSetter.commitValue();
            this.flipbook.setFrameRate(this.fpsSetter.getValue());

            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(flipbook.getFrameTime()),
                event -> {
                    flipbook.forward(true);
                    populateTimeline();
                    updateThumbnails();
                    setFrameCount();

                });

            Timeline timeline = new Timeline(keyFrame);

            timeline.setCycleCount(flipbook.getNumFrames() - flipbook.getCurFrameNum());

            timeline.play();

            playBtnIcon.setImage(new Image("ui/resources/icons/baseline_pause_black_24dp.png"));
            timeline.setOnFinished(e -> {
                isAnimating = false;
                flipbook.setOnionSkinning(onionSkinningOn);
                playBtnIcon.setImage(new Image("ui/resources/icons/baseline_play_arrow_black_24dp.png"));
            });


            setFrameCount();
        }


        //sets frame count in the bottom container
        public void setFrameCount() {
            frameNumLabel.setText((flipbook.getCurFrameNum()+1)+"");
        }

        public void handleMousePressed(MouseEvent e) {
            GraphicsContext gc = flipbook.getGraphicsContext(Character.getNumericValue(layerPicker.getValue().charAt(layerPicker.getValue().length()-1))-1);
           
            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());

        }

        public void handleMouseReleased(MouseEvent e) {
            //flipbook.saveFrame();
            addThumbnails(this.flipbook.getCurFrameNum());
            updateThumbnails();
            System.out.println(flipbook.getCurFrameNum());
        }

        public void handleMouseDragged(MouseEvent e) {
            GraphicsContext gc = flipbook.getGraphicsContext(Character.getNumericValue(layerPicker.getValue().charAt(layerPicker.getValue().length()-1))-1);
            gc.setLineWidth(this.pencilWidthSlider.getValue());
            if (this.activeTool == "Eraser") {
                gc.clearRect(e.getX()-5, e.getY()-5, 10, 10);
            }
            else if (this.activeTool == "Pencil") {
                gc.setStroke(this.colorPicker.getValue());

                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }

            /*
            addThumbnails(this.flipbook.getCurFrameNum());

            updateThumbnails();
            */

            
            
            
           
        }

        public void updateThumbnails() {
        	
        	if(flipbook.getCurFrameNum() >= 20) {
        		return;
        	}
        	
        	//if you're not on index 0, there is a prev frame
            if (flipbook.getCurFrameNum() != 0) {
                this.prevFrameImg.setImage(thumbnails.getThumbnailAt(this.flipbook.getCurFrameNum()-1).getThumbnailImage());
                prevFrameImg.setVisible(true);
            }
            else {
                prevFrameImg.setVisible(false);
            }
            
            //if you're not the last frame, there is a next frame
            if (flipbook.getCurFrameNum() != this.flipbook.getNumFrames()-1) {
            	
                this.nextFrameImg.setImage(this.thumbnails.getThumbnailAt(this.flipbook.getCurFrameNum()+1).getThumbnailImage());
                nextFrameImg.setVisible(true);

            }
           
            else {
                nextFrameImg.setVisible(false);
            }
            
           
           timelineBox.getChildren().get(flipbook.getCurFrameNum()).setEffect(new DropShadow());
            
            
        }

    @FXML
    protected void setPencil() {
        this.activeTool = "Pencil";
        flipbookPane.setCursor(new ImageCursor(new Image("ui/resources/icons/pen-solid.png"), 0, 64));
    }
    @FXML
    protected void setEyedropper() {
        this.activeTool = "Eyedropper";
        flipbookPane.setCursor(Cursor.CROSSHAIR);
    }
    @FXML
    protected void setEraser() {
        this.activeTool = "Eraser";
        flipbookPane.setCursor(Cursor.OPEN_HAND);
    }

    // Loads an image to the current layer of the current frame.
    @FXML
    protected void loadImg() {
        //opens a window to allow you to pick an image file
        FileChooser openImg = new FileChooser();
        openImg.setTitle("Open");
        openImg.getExtensionFilters().add(new ExtensionFilter("Image file", "*.png", "*.jpg"));
        File file = openImg.showOpenDialog(myStage);

        this.flipbook.getGraphicsContext(0).drawImage(new Image(file.toURI().toString()), 0, 0, this.flipbook.getCanvasWidth(), this.flipbook.getCanvasHeight());
        addThumbnails(this.flipbook.getCurFrameNum());
    }

    /*
        Begin File Menu Options
     */
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
    /*
        End File Menu Options
     */

    /*
        Begin Edit Menu Options
     */
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
    /*
        End Edit Menu Options
     */


    /*
        Begin View Menu Options
     */
    @FXML
    protected void toggleOnionSkinning() {
            this.onionSkinningOn = !this.onionSkinningOn;
            this.flipbook.setOnionSkinning(onionSkinningOn);
    }
    /*
        End View Menu Options
     */

    /*
        Begin Player Controls
     */
    @FXML
    protected void play() {
        if (!isAnimating) {
            //TODO: onion-skinning isn't being disabled like it should be. But then again, not sure if its better that way
            this.flipbook.setOnionSkinning(false);
            animate();
            this.flipbook.setOnionSkinning(onionSkinningOn);
        }
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
        addThumbnails(curFrame);
        if (curFrame == this.flipbook.getNumFrames() - 1) {
            this.flipbook.addFrame();
            addThumbnails(curFrame + 1);
        }
        // Does the frame need to be saved?
        // this.flipbook.saveFrame();
        flipbook.forward(false);
        seekTo(curFrame + 1);
    }
    /*
        End Player Controls
     */

    // TODO: if animating halt animation or make button unresponsive
    protected void seekTo(int frameIndex) {
        this.flipbook.setFrame(frameIndex);
        updateThumbnails();
        setFrameCount();
    }

    // separated this from seekTo() to fix order of operations for nextFrame()
    protected void addThumbnails(int curFrame){
    	if(curFrame <= 20) {
    	
        this.thumbnails.insert(new Thumbnail(flipbook.generateFrameNode(curFrame)), curFrame);
        
    	}
        populateTimeline();
    	

    }
       
    
}