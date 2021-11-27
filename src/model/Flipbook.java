package model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class Flipbook {
    //small class to contain the base64 img and frame visibility
    //also includes the frame generation function

    private class LayerData{
        String imgString;
        boolean isVisible;
        Layer layer;

        LayerData(String img, boolean visible){
            this.imgString = img;
            this.isVisible = true;

            this.layer = new Layer(canvasWidth, canvasHeight);

        }

    }


    private class FrameData{

        String imgString;
        LayerData activeLayer;
        boolean isVisible;
        double opacity;

        ArrayList<LayerData> layers;

        FrameData(String img, boolean visible, double opacity){

            this.isVisible = visible;
            this.opacity = opacity;
            this.layers = new ArrayList<LayerData>();
            layers.add(new LayerData(img, visible));
            layers.add(new LayerData(img, visible));
            layers.add(new LayerData(img, visible));
            this.activeLayer = layers.get(0);
        }

        public ArrayList<LayerData> getLayers() {
            return this.layers;
        }

        public void setActiveLayer(int curLayer) {this.activeLayer = this.layers.get(curLayer); }

        public Group generateGroup() {
            Group g = new Group();

            for(LayerData l: layers) {

                if(l.isVisible) {

                    l.layer.getGraphicsContext().drawImage(new Image(l.imgString), 0, 0);
                    g.getChildren().add(l.layer);

                }

            }

            g.setOpacity(this.opacity);

            return g;
        }
    }



    // a place to store the frames
    private ArrayList<FrameData> frames;



    //basic variables
    private String bookName;
    private int canvasWidth;
    private int canvasHeight;

    //frame rate in fps
    private int frameRate = 10;
    private boolean onionSkinningEnabled = true;

    //frame length in milliseconds
    private final long frameTime = Math.round((1.0/frameRate) * 1000);

    //current frame index
    private int curFrame = 0;

    //this object holds the frame canvases and allows them to be displayed
    private final Group group;
    public void setFrameRate(int fr) {
        this.frameRate = fr;
    }

    public Flipbook(int canvasWidth, int canvasHeight, String bookName){

        this.frames = new ArrayList<FrameData>();

        this.bookName = bookName;

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        this.group =  new Group();

    }

    public void setOnionSkinning(boolean os) {
        this.onionSkinningEnabled = os;
        setFrame(this.curFrame);
    }

    public void deleteFrame(int frameNum) {
        this.frames.remove(frameNum);
    }


    //sets frame visibility to false. When the update function gets called, this will 'clear the screen'
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

            //onionSkinningEnabled = true;
            if(onionSkinningEnabled) {
                //how much we need to split the opacity range by for each frame
                //The minimum amount of frames should be 1. NEVER SET THIS TO 0.
                //Setting it to 0 will cause a divide by 0 when calculating the opacity delta
                //in the setFrame function
                int numPrevFramesToShow = 1;
                double opacityDelta = 1.0/(numPrevFramesToShow);

                //what the spread from first onion frame opacity to last frame opacity is
                //opacity variables; they determine what the initial onion frame's opacity fill be
                //and help calculate the loss of opacity as you see further frames
                double maxOnionOpacity = 0.4;
                double minOnionOpacity = 0.1;
                double opacityRange = maxOnionOpacity - minOnionOpacity;


                //the step between each frame
                opacityDelta *= opacityRange;

                for(int i = 0; i <= numPrevFramesToShow && (frameNumber - i >= 0); i++) {


                    frames.get(frameNumber - i).isVisible = true;

                    //do not change the first frame
                    if(i != 0) {

                        //the opacity gets lower the further you go from the current frame
                        //the further we go back, the more opacity we subtract
                        frames.get(frameNumber - i).opacity = (maxOnionOpacity - (opacityDelta*(i-1)));



                    }
                }
            }

            //if onion skinning is off, we just show the selected frame only
            else {
                frames.get(frameNumber).isVisible = true;
            }
            this.curFrame = frameNumber;

            update();

        }

    }


    //clear the group, add visible frames to the group
    public void update() {

        group.getChildren().clear();

        for(FrameData f: frames) {
            if(f.isVisible) {
                group.getChildren().add(f.generateGroup());
            }
        }
    }


    //makes a blank frame and adds it where the function is called
    public void addFrame() {

        Layer layer = new Layer(canvasWidth, canvasHeight);
        FrameData frameData = new FrameData(generateImgURL(layer), false, 0);

        System.out.println(frameData.imgString);

        //if we have no frames, add a frame, set the frame on the canvas, and
        if(frames.size() < 1) {
            frames.add(frameData);
            setFrame(curFrame);
        }

        else {
            for(LayerData l: frames.get(curFrame).layers) {
                l.imgString = generateImgURL(l.layer);
            }

            frames.add(curFrame+1, frameData);
            setFrame(curFrame);
            forward(false);
        }



    }

    //checks if there is a frame to move forward to, and does it if so
    public void forward(boolean isAnimating) {
        long tInit = System.nanoTime();

        //TODO: Maybe add a changesMade variable so that we don't always have to generate a URL
        if(curFrame + 1 < frames.size()) {

            //we need to save the frame we're on, then we can advance
            if(!isAnimating) {
                saveFrame();
            }

            curFrame++;
            setFrame(curFrame);

        }

        long tFinish = System.nanoTime();

        System.out.println("Time Forward(): " + ((tFinish-tInit) / 1000000));

    }

    //checks if there is a frame to move back to, and does it if so
    public void backward() {

        if(curFrame - 1 >= 0 ) {

            //we need to save the frame we're on
            saveFrame();

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
    //Serializable would create an easy interface to saving
    //it could be the end game for the save file
    public String createFileForSave() {

        ArrayList<String> convertedImages = new ArrayList<String>();

        for(FrameData f: frames) {
            convertedImages.add(f.imgString);
        }

        //creates string with each element on its own line
        String toSave = "";

        toSave += bookName + "\n";
        toSave += canvasWidth + "\n";
        toSave += canvasHeight + "\n";

        //this value is kind of redundant; it's not needed,
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
            //could be useful, but not really necessary
            //after all frames are added, you can just get frames.size() to see how many frames there are
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


    //gets a frame, generates the img URL
    public String generateImgURL(Node n) {

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        //snapshot takes an 'image' of the canvas so that we can save it for later
        WritableImage writableImage = n.snapshot(params, null);

        //setting up base 64 conversion
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", baos);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //making into a URL, add it to the arraylist
        String imageString = "file:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        return imageString;
    }

    //saves the current frame back to it's frameData object
    //makes sure we save changes to the frame when we draw on it, etc
    public void saveFrame() {

        long tInit = System.nanoTime();

        for(LayerData l: frames.get(curFrame).layers) {
            l.imgString = generateImgURL(l.layer);
            System.out.println(l.imgString);
        }

        long tFinish = System.nanoTime();

        System.out.println("Time saveFrame(): " + ((tFinish-tInit) / 1000000));

    }

    public ArrayList<FrameData> getFrames() {
        return this.frames;
    }


    //basic getters
    public int getNumFrames() {
        return frames.size();
    }

    public long getFrameTime() {
        return frameTime;
    }

    public int getCurFrameNum() {
        return curFrame;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public Group getGroup() {
        return group;
    }

    public GraphicsContext getGraphicsContext(int layerNum) {
        return frames.get(curFrame).layers.get(layerNum).layer.getGraphicsContext();
    }
}
