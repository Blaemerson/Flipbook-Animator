package model;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.LinkedList;


public class Thumbnail {
    // Indices should correspond directly with frame indices in the flipbook.
    private List<Image> thumbnails = new LinkedList<>();

    SnapshotParameters params = new SnapshotParameters();
    
    
    public Thumbnail(Node frameToConvert) {
    	params.setFill(Color.TRANSPARENT);
        Image thumbnail = frameToConvert.snapshot(params, null);
        thumbnails.add(thumbnail);
    }

    // Alternative constructor for converting an entire flipbook to thumbnails
    public Thumbnail(List<Node> framesToConvert) {
    	params.setFill(Color.TRANSPARENT);
        thumbnails = new LinkedList<>();
        for (Node f : framesToConvert) {
            thumbnails.add(f.snapshot(params, null));
        }
    }

    public Image convert(Node frame) {
    	params.setFill(Color.TRANSPARENT);
        return frame.snapshot(params, null);
    }

    // remove the thumbnail for the frame specified by the index <frame>
    public void remove(int frame) {
        thumbnails.remove(frame);
    }

    // insert a thumbnail <thumb> at <index>, replacing what was previously there
    public void insert(Image thumb, int index) {
        if (thumbnails.size() > index) {
            thumbnails.remove(index);
        }
        thumbnails.add(index, thumb);
    }

    // shift thumbnails to the right when adding a new frame
    public void shiftThumbnails(int index) {
        thumbnails.add(index, null);
    }

    public List<Image> getThumbnails() {
        return this.thumbnails;
    }

    public Image getThumbnailAt(int index) {
        return this.thumbnails.get(index);
    }

}
