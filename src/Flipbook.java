import java.util.*;
import javafx.scene.*;
import javafx.scene.paint.*;

public class Flipbook {
	
	//TODO: include frame state 
	private class FrameData{
		Frame frame;
		boolean isVisible;
		
		FrameData(int canvasWidth, int canvasHeight, boolean isVisible){
			this.frame = new Frame(canvasWidth, canvasHeight);
			this.isVisible = isVisible;
		}
	}
	
	
	
	private ArrayList<FrameData> frames;
	private String bookName;
	private int canvasWidth;
	private int canvasHeight;
	private int curFrame = 0;
	private Group group;
	
	Flipbook(int canvasWidth, int canvasHeight, String bookName){
		
		this.frames = new ArrayList<FrameData>();
		this.bookName = bookName;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.group =  new Group();
		//Scene scene = new Scene(this.group, canvasWidth, canvasHeight, Color.rgb(0, 0, 0, 0));
	}
	
	public void addFrame() {
		FrameData frameData = new FrameData(canvasWidth, canvasHeight, true);
		frames.add(frameData);
		group.getChildren().add(frameData.frame.getCanvas());
	}
	
	public Group getGroup() {
		return group;
	}
	
}
