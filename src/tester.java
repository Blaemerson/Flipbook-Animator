import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
	
	//making buttons
	Button saveButton = new Button("Save");
	Button openButton = new Button("Open");
	
	final String appTitle = "Flipbook Proto 1";
	
	
	
    @Override
    public void start(Stage stage) {
    	
    	stage.setTitle(appTitle);
    	
    	//container instantiation
    	pane = new BorderPane();
    	pane.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
    	
    	flipbookPane = new Pane();
    	flipbookPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    	
    	frameCountDisplay = new HBox();
    	frameCountDisplay.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    	
    	toolbar = new ToolBar();
    	
    	myStage = stage;
    	//the scene is what holds the pane, which holds all the other nodes
        Scene scene = new Scene(pane, 1024, 768);
    	
        //add buttons to tool bar, add frame label to HBox
    	toolbar.getItems().addAll(saveButton, openButton);
    	frameCountDisplay.getChildren().add(currentFrame);
    	
        
    	//////////////////////////////////////////
    	flipbook = new Flipbook(640, 480, "test");
    	flipbookPane.getChildren().add(flipbook.getGroup());
    	flipbookPane.setMaxSize(640, 480);
    	
    	// setting position of nodes
   	    pane.setTop(toolbar);
        pane.setCenter(flipbookPane);
        pane.setBottom(frameCountDisplay);
    	
        
        //add a frame by default and select it
   	 	flipbook.addFrame();
        flipbook.setFrame(0);
        currentFrame.setText(frameMessage + flipbook.getFrameNumber());
        
        
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
   	 		
        	this.save();
        	
        });
   	 	
      	//run open function when button activated
   	 	openButton.setOnAction((e)->{
   	
         	this.open();
         	
         });
   	 	
   	 	
   	 	//TODO: Tool Keybinds should go here eventually
   	 	pane.setOnKeyPressed(e ->{
   	 		
   	 		//go forward a frame when ] is pressed
        	if(e.getCode() == KeyCode.CLOSE_BRACKET) {
        		flipbook.forward();
        		currentFrame.setText(frameMessage + flipbook.getFrameNumber());
        	}
        	
        	//go back a frame when [ is pressed
        	else if(e.getCode() == KeyCode.OPEN_BRACKET) {
        		flipbook.backward();
        		currentFrame.setText(frameMessage + flipbook.getFrameNumber());
        	}
        	
        	//create frame when \ is pressed, go forward to it
        	else if(e.getCode() == KeyCode.BACK_SLASH) {
        		flipbook.addFrame();
        		flipbook.forward();
        		currentFrame.setText(frameMessage + flipbook.getFrameNumber());
        	}
        	
        	
        });
   	
   	 
   	 	/*
   	 	 
   	 	//////////////////
   	 	END EVENT HANDLERS
   	 	//////////////////
   	 	 
   	 	*/
   	 	
   	 	
    }

   
    
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
    	
    	//result string to send back to the Flipbook
		String fileForOpen = "";
    	
    	//opens a window to allow you to pick a .flip file
    	 FileChooser openfile = new FileChooser();
         openfile.setTitle("Open");
         openfile.getExtensionFilters().add(new ExtensionFilter("Flip file", "*.flip"));
         
         File file = openfile.showOpenDialog(myStage);
         
         
         //read every line, add a newline between each line so we know
         //where each bit of data begins and ends
         if (file != null) {
             try {
            	 
            	 BufferedReader in = new BufferedReader(new FileReader(file));
            	 
            	 while(in.ready()) {
            		 
            		 fileForOpen += in.readLine() + "\n";
            	 }
            	 
            	 //always close file streams
            	 in.close();
            	 
            	 //have flipbook parse string back into the saved state
            	 //then set the visible frame equal to the first frame
            	 flipbook.openFile(fileForOpen);
            	 flipbook.setFrame(0);
            	 currentFrame.setText(frameMessage + flipbook.getFrameNumber());
            	 
             } 
             
             catch (IOException ex) {
            	 
                 System.out.println("Error opening file, or reading data.");
             }
             
         }
         
    }



	public static void main(String[] args) {
		
		launch();
	}

    
}
