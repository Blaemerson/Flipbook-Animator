import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;



//TODO: Rework Save File Structure into JSON??


public class Flipbook {
	
	//small class to contain the base64 img and frame visibility
	//also includes the frame generation function
	private class FrameData{
		String imgString;
		boolean isVisible;
		double opacity;
		
		
		FrameData(String img, boolean visible, double opacity){
			this.imgString = img;
			this.isVisible = visible;
			this.opacity = opacity;
		}
		
		public Frame generateFrame() {
			Frame f = new Frame(canvasWidth, canvasHeight);
			f.getGraphicsContext().drawImage(new Image(imgString), 0, 0);
			f.setOpacity(this.opacity);
			
			return f;
		}
		
	
		
		
	}
	
	
	
	// a place to store the frames
	private ArrayList<FrameData> frames;
	
	
	
	//basic variables
	private String bookName;
	private int canvasWidth;
	private int canvasHeight;
	private double maxOnionOpacity = 0.6;
	private double minOnionOpacity = 0.1;
	
	//frame rate in fps
	private int frameRate = 20;
	
	//in milliseconds
	private long frameTime = Math.round((1.0/frameRate) * 1000);
	
	//min: 1
	private int numPrevFramesToShow = 2;
	
	//Doesn't work yet on build 2a (this is 2a)
	private boolean onionSkinningEnabled = false;
	
	//current frame
	private int curFrame = 0;
	
	//this object holds the frame canvases and allows them to be displayed
	private Group group;
	
	Flipbook(int canvasWidth, int canvasHeight, String bookName){
		
		this.frames = new ArrayList<FrameData>();
		
		this.bookName = bookName;
		
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		
		this.group =  new Group();
		
	}
	
	
	//clears group of all frames, which effectively clears screen
	private void clearScreen() {
	
		for(FrameData f: frames) {
			f.isVisible = false; 
			f.opacity = 1;
		}
		
		
		
	}
	
	//allows user to pick a frame and display it on the screen
	public void setFrame(int frameNumber) {
		
		if(frameNumber < frames.size()) {
			clearScreen();
			
			if(onionSkinningEnabled) {
				double opacityDelta = 1.0/(numPrevFramesToShow);
				double opacityRange = maxOnionOpacity - minOnionOpacity;
				
				opacityDelta *= opacityRange;
			
				for(int i = 0; i <= numPrevFramesToShow && (frameNumber - i >= 0); i++) {
		
					
					    frames.get(frameNumber - i).isVisible = true;
					    
					    if(i != 0) {
					    	
					    	frames.get(frameNumber - i).opacity = (maxOnionOpacity - (opacityDelta*(i-1)));
					    	System.out.println((maxOnionOpacity - (opacityDelta*(i-1))));
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
		
		group.getChildren().clear();
		
		for(FrameData f: frames) {
			if(f.isVisible) {
				Frame frame = f.generateFrame();
				group.getChildren().add(frame);
				
			}
		}
		
	}
	
	
	//makes a blank frame and adds it where the function is called
	public void addFrame() {
	
		Frame frame = new Frame(canvasWidth, canvasHeight);
		FrameData frameData = new FrameData(generateImgURL(frame), false, 1);
		
		if(frames.size() < 1) {
			frames.add(frameData);
			setFrame(curFrame);
			forward();
		}
		
		else {
			
			Frame f = (Frame)group.getChildren().get(group.getChildren().size()-1);
			String img = generateImgURL(f);
			frames.get(curFrame).imgString = img;
			
			frames.add(curFrame+1, frameData);
			setFrame(curFrame);
			forward();
		}
			
		
		
	}
	
	//checks if there is a frame to move forward to, and does it if so
	public void forward() {
		
		
		if(curFrame + 1 < frames.size()) {
			
			Frame f = (Frame)group.getChildren().get(group.getChildren().size()-1);	
			String img = generateImgURL(f);
			frames.get(curFrame).imgString = img;
			
			curFrame++;
			setFrame(curFrame);
			
		}
		
		
		
	}
	
	//checks if there is a frame to move back to, and does it if so
	public void backward() {
		
		if(curFrame - 1 >= 0 ) {
		
		
			Frame f = (Frame)group.getChildren().get(group.getChildren().size()-1);
			
			String img = generateImgURL(f);
			
			frames.get(curFrame).imgString = img;
			
			
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
			
			convertedImages.add(f.imgString);
             
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
	public void openFile(File file) {
		
		//we need to clear the screen before we load a file, we also need to clear the frames arraylist
		clearScreen();
		frames = new ArrayList<FrameData>();
		curFrame = 0;
		
		
		//parsing values into their respective variables
		try {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		bookName = reader.readLine();
		canvasWidth = Integer.parseInt(reader.readLine());
		canvasHeight = Integer.parseInt(reader.readLine());
		
		//this sc.nextLine() is ignoring the line that says how many frames there are
		reader.readLine();
	
		while(reader.ready()) {
			String imageStr = reader.readLine();
			FrameData frame = new FrameData(imageStr, false, 1);
			
			frames.add(frame);
			
		
		}
		
		//always close file streams
		reader.close();
		
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	public String generateImgURL(Frame f) {
		
		
		
		 //snapshot takes an 'image' of the canvas so that we can save it for later
		WritableImage writableImage = f.snapshot(null, null);
        
        //setting up base 64 conversion
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", baos);
            
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //making into a URL, add it to the arraylist
        String imageString = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        
        return imageString;
		
		
	}
	
	
	public int getNumFrames() {
		return frames.size();
	}
	
	public long getFrameTime() {
		return frameTime;
	}
	
	public int getCurFrameNum() {
		return curFrame;
	}

	
	//basic getters

	public Group getGroup() {
		return group;
	}

	public int getFrameNumber() {
		return curFrame;
	}

}




