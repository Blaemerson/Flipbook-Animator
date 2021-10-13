import java.util.*;
import javafx.scene.*;

public class Flipbook {
	
	//TODO: include frame state 
	private class FrameData{
		Frame frame;
		boolean isVisible;
	
		
	//FrameData is made to keep the frame and any data pertaining to a specific frame in one place	
		FrameData(int canvasWidth, int canvasHeight, boolean isVisible){
			this.frame = new Frame(canvasWidth, canvasHeight);
			this.isVisible = isVisible;
		}
	}
	
	
	// a place to store the frames
	private ArrayList<FrameData> frames;
	
	
	private String bookName;
	private int canvasWidth;
	private int canvasHeight;
	
	//TODO: make frame change functionality
	private int curFrame = 0;
	
	//this object holds the frame canvases and allows them to be displayed
	//the way this is structured may help make onion skinning easier later on
	private Group group;
	
	Flipbook(int canvasWidth, int canvasHeight, String bookName){
		
		
		this.frames = new ArrayList<FrameData>();
		
		this.bookName = bookName;
		
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		
		this.group =  new Group();
		
	}
	
	//makes a blank frame, and then adds it to the group to be displayed
	public void addFrame() {
	
		//makes a frame data object, which makes it's own frame
		FrameData frameData = new FrameData(canvasWidth, canvasHeight, true);
		frames.add(frameData);
		
		//add the created frame's canvas to the group to be displayed
		group.getChildren().add(frameData.frame.getCanvas());
	}
	
	
	public Group getGroup() {
		return group;
	}
	
	
	
}
