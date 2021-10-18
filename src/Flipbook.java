import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Flipbook {
	
	//TODO: include frame state 
	//FrameData is made to keep the frame and any data pertaining to a specific frame in one place
	private class FrameData{
		Frame frame;
		boolean isVisible;
	
		FrameData(int canvasWidth, int canvasHeight, boolean isVisible){
			this.frame = new Frame(canvasWidth, canvasHeight);
			this.isVisible = isVisible;
		}
		
	}
	
	
	
	
	// a place to store the frames and relevant data
	private ArrayList<FrameData> frames;
	
	private String bookName;
	private int canvasWidth;
	private int canvasHeight;
	private double maxOnionOpacity = 0.7;
	private double minOnionOpacity = 0.3;
	
	//min: 1
	private int numPrevFramesToShow = 2;
	private boolean onionSkinningEnabled = true;
	
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
	
	
	//clears group of all frames, which effectively clears screen
	// isVisible isn't being used yet, but it's there if we want it
	private void clearScreen() {
		
//		group.getChildren().remove(0, group.getChildren().size());
	
		for(FrameData f: frames) {
			f.isVisible = false;
			f.frame.getCanvas().setOpacity(1);
		}
		
		
	}
	
	//allows user to pick a frame and display it on the screen
	public void setFrame(int frameNumber) {
		
		if(frameNumber < frames.size()) {
			clearScreen();
//			group.getChildren().add(frames.get(frameNumber).frame.getCanvas());
			
			if(onionSkinningEnabled) {
				double opacityDelta = 1.0/(numPrevFramesToShow);
				double opacityRange = maxOnionOpacity - minOnionOpacity;
				
				opacityDelta *= opacityRange;
			
				for(int i = 0; i <= numPrevFramesToShow && (frameNumber - i >= 0); i++) {
		
					
					    frames.get(frameNumber - i).isVisible = true;
					    
					    if(i != 0) {
					    	
					    	frames.get(frameNumber - i).frame.getCanvas().setOpacity(maxOnionOpacity - (opacityDelta*(i-1)));
					    	
					    }
					    
				}
						
			}
			
			else {
				
				frames.get(frameNumber).isVisible = true;
			
			}
			
			update();
			
		}
		
	}
	
	
	public void update() {
		
		group.getChildren().remove(0, group.getChildren().size());
		
		
		for(FrameData f: frames) {
			if(f.isVisible)
				group.getChildren().add(f.frame.getCanvas());
		}
		
		
		
		
	}
	
	
	
	
	
	//makes a blank frame, and then adds it to the group to be displayed
	public void addFrame() {
	
		//makes a frame data object, which makes it's own frame
		FrameData frameData = new FrameData(canvasWidth, canvasHeight, true);
		frames.add(frameData);
		
		
		
		
		update();
		
		//add the created frame's canvas to the group to be displayed
//		group.getChildren().add(frameData.frame.getCanvas());
	}
	
	//checks if there is a frame to move forward to, and does it if so
	public void forward() {
		
		if(curFrame + 1 < frames.size()) {
			
			curFrame++;
			setFrame(curFrame);
		}
		
	}
	
	//checks if there is a frame to move back to, and does it if so
	public void backward() {
		
		if(curFrame - 1 >= 0 ) {
			
			curFrame--;
			setFrame(curFrame);
		}
				
	}
	

	
	
	/* This function grabs info about the flipbook
	 * and puts it in a string, where it is saved to
	 * a file. Images are encoded in base 64 and placed
	 * in a URL, where they can be used in the open process.
	 */
	
	//TODO: Implement Serializable??
	public String createFileForSave() {
		
		ArrayList<String> convertedImages = new ArrayList<String>();
		
		for(FrameData f: frames) {
			
			 WritableImage writableImage = new WritableImage(canvasWidth, canvasHeight);
			 
			 //snapshot takes an 'image' of the canvas so that we can save it for later
             f.frame.getCanvas().snapshot(null, writableImage);
             RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
             
             //setting up base 64 conversion
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             try {
                 ImageIO.write(renderedImage, "png", baos);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             
             //making into a URL, add it to the array
             String imageString = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
             convertedImages.add(imageString);
             
		}
		
		
		//creates string with each element on it's own line
		String toSave = "";
		
		toSave += bookName + "\n";
		toSave += canvasWidth + "\n";
		toSave += canvasHeight + "\n";
		
		//this value is kind of redundant; it's not needed
		//but it may be nice to have in the future?
		toSave += convertedImages.size() + "\n";
		
		//add base64 encoded strings onto result
		for(String s: convertedImages) {
			toSave += s + "\n";
		}
		
		return toSave;
		
	}
	
	
	//takes string, parses info out and stores resulting frames in the frames arraylist
	public void openFile(String file) {
		
		//we need to clear the screen before we load a file, we also need to clear the frames arraylist
		clearScreen();
		frames = new ArrayList<FrameData>();
		curFrame = 0;
		
		
		//parsing values into their respective variables
		Scanner sc = new Scanner(file);
		
		bookName = sc.nextLine();
		canvasWidth = Integer.parseInt(sc.nextLine());
		canvasHeight = Integer.parseInt(sc.nextLine());
		
		//this sc.nextLine() is ignoring the line that says how many frames there are
		sc.nextLine();
		
		while(sc.hasNextLine()) {
			String imageStr = sc.nextLine();
			FrameData data = new FrameData( canvasWidth, canvasHeight, true);
			
			//as it turns out, JavaFX Image accepts URLs as an argument to make an image
			//this makes our job really easy
			data.frame.getCanvas().getGraphicsContext2D().drawImage(new Image(imageStr), 0, 0);
			
			frames.add(data);
		}
		
		//always close file streams
		sc.close();
		
		
	}
	
	
	//basic getters

	public Group getGroup() {
		return group;
	}

	public int getFrameNumber() {
		return curFrame;
	}

	
	
	
}




