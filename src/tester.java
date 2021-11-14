import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.stage.*;



public class tester extends Application {
	
	Flipbook flipbook;
	
	
	// frame counter at bottom of application
	String frameMessage = "Current Frame: ";
	Label currentFrame = new Label(frameMessage + "--");
	
	//content containers
	BorderPane pane;
	ToolBar toolbar;
	Stage myStage;
	HBox frameCountDisplay;
	Pane flipbookPane;
	Canvas drawLayer;
	
	
	//making buttons
	Button newButton = new Button("New");
	Button saveButton = new Button("Save");
	Button openButton = new Button("Open");
	
	//program name
	final String appTitle = "Flipbook Proto 2a";
	
	//prevents actions from occuring when there are potential conflicts
	boolean openFlipbook = false;
	boolean isAnimating = false;
	
    @Override
    public void start(Stage stage) {
    	
    	//window title
    	stage.setTitle(appTitle);
    	
    	//container instantiation setting backgrounds
    	pane = new BorderPane();
    	pane.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
    	
    	flipbookPane = new Pane();
    	flipbookPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    	
    	
    	frameCountDisplay = new HBox();
    	frameCountDisplay.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    	
    	
    	//toolbar up top to store buttons
    	toolbar = new ToolBar();
    	
    	//setting equal so the stage can be referenced in other functions
    	myStage = stage;
    	
    	//the scene is what holds the pane, which holds all the other nodes
        Scene scene = new Scene(pane, 1024, 768);
    	
        //add buttons to tool bar, add frame label to HBox
    	toolbar.getItems().addAll(newButton, openButton, saveButton);
    	frameCountDisplay.getChildren().add(currentFrame);
    	
        
    	//creating flipbook (which makes no sense because newFile hasn't been called
    	//but it's a prototype, who really cares?
    	
    	
    	
    	// setting position of nodes
   	    pane.setTop(toolbar);
        pane.setBottom(frameCountDisplay);
       
               
        //and the stage is the top level container; it's the actual window
        stage.setScene(scene);
        stage.show();
        
        /*
         
        /////////////////////
        START EVENT HANDLERS
        /////////////////////
         
        */
        
        //run save function when button activated
   	 	saveButton.setOnAction((e)->{
   	 		
        	save();
        	
        });
   	 	
      	//run open function when button activated
   	 	openButton.setOnAction((e)->{
   	
         	open();
         	
         });
   	 	
   	 	//run newFile function when button activated
   	 	newButton.setOnAction((e) ->{
   	 		
   	 		newFile();
   	 		
   	 	});
   	 	
   	 	
   	 	//TODO: Tool Keybinds should go here eventually
   	 	pane.setOnKeyPressed(e ->{
   	 		
   	 		//go forward a frame when ] is pressed
        	if(e.getCode() == KeyCode.CLOSE_BRACKET && openFlipbook) {
        		flipbook.forward(false);
        		setFrameCount(flipbook.getFrameNumber());
        	}
        	
        	//go back a frame when [ is pressed
        	else if(e.getCode() == KeyCode.OPEN_BRACKET && openFlipbook) {
        		flipbook.backward();
        		setFrameCount(flipbook.getFrameNumber());
        	}
        	
        	//create frame when \ is pressed, go forward to it
        	else if(e.getCode() == KeyCode.BACK_SLASH && openFlipbook) {
        		
        		flipbook.addFrame();
        		setFrameCount(flipbook.getFrameNumber());
        	}
        	
        	//animate frames P is pressed, speed determined by flipbook variable frameRate
        	else if(e.getCode() == KeyCode.P && openFlipbook && !isAnimating) {
        			
        				animate();
        				setFrameCount(flipbook.getFrameNumber());
        			
        		}
        	}
   	 	);
      
    
   	 
   	 	/*
   	 	 
   	 	//////////////////
   	 	END EVENT HANDLERS
   	 	//////////////////
   	 	 
   	 	*/
   	 	
   	 	
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
         
         pane.setCenter(flipbookPane);
    	 flipbook.setFrame(0);
    	 setFrameCount(flipbook.getFrameNumber());
    	 openFlipbook = true;
         
    }

    //makes the first frame and allows other keyboard events to occur
    //TODO: Add new file menu, allow user to change canvas size at that time
	public void newFile() {
		
		flipbook = new Flipbook(640, 480, "test");
		
    	flipbookPane.setMaxSize(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());
		
    	drawLayer = new Canvas(flipbook.getCanvasWidth(), flipbook.getCanvasHeight());
    	
    	drawLayer.setOnMousePressed(e -> {handleMousePressed(e);});
    	
    	drawLayer.setOnMouseDragged(e->{
            
          handleMouseDragged(e);
           
	   });	
    	
    	
    	flipbookPane.getChildren().addAll(flipbook.getGroup(), drawLayer);
    	
		pane.setCenter(flipbookPane);
		flipbook.addFrame();
		setFrameCount(flipbook.getFrameNumber());
		
		openFlipbook = true;
		
	}
	
	
	//uses frameRate in flipbook to call the forward function at timed intervals
	public void animate() {
	
		isAnimating = true;
	
		KeyFrame keyFrame = new KeyFrame(Duration.millis(flipbook.getFrameTime()), 
	            event -> {
	            	
	            	
	            	
	            	flipbook.forward(true); 
	            	
	            	
	            	
	            	
	            	setFrameCount(flipbook.getFrameNumber()); 
	            	System.out.println("Frame #: " + flipbook.getFrameNumber());
	            	
	            	
	            	
	            	});
		
	        Timeline timeline = new Timeline(keyFrame);
	    
		timeline.setCycleCount(flipbook.getNumFrames() - flipbook.getCurFrameNum());
		
		timeline.play();
		
		timeline.setOnFinished(e -> {isAnimating = false;});
	}


	//sets frame count in the bottom container
	public void setFrameCount(int frameNumber) {
		currentFrame.setText(frameMessage + flipbook.getFrameNumber());
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
	
	
	public static void main(String[] args) {
		
		launch();
	}

    
}
