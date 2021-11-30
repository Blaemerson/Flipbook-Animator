package com.onionskin.onionskin.flipbookanimator.model;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.LinkedList;


public class Thumbnail {
    // Indices should correspond directly with frame indices in the flipbook.
    private List<Image> thumbNails = new LinkedList<>();

    SnapshotParameters params = new SnapshotParameters();

    public Thumbnail(Node frameToConvert) {
        params.setFill(Color.TRANSPARENT);
        Image thumbnail = frameToConvert.snapshot(params, null);
        thumbNails.add(thumbnail);
    }

    // Alternative constructor for converting an entire flipbook to thumbnails
    public Thumbnail(List<Node> framesToConvert) {
        params.setFill(Color.TRANSPARENT);
        thumbNails = new LinkedList<>();
        for (Node f : framesToConvert) {
            thumbNails.add(f.snapshot(params, null));
        }
    }

    public Image convert(Node frame) {
        params.setFill(Color.TRANSPARENT);
        return frame.snapshot(params, null);
    }

    // remove the thumbnail for the frame specified by the index <frame>
    public void remove(int frame) {
        thumbNails.remove(frame);
    }

    // insert a thumbnail <thumb> at <index>, replacing what was previously there
    public void insert(Image thumb, int index) {
        if (thumbNails.size() > index) {
            thumbNails.remove(index);
        }
        thumbNails.add(index, thumb);
    }

    // shift thumbnails to the right when adding a new frame
    public void shiftThumbnails(int index) {
        thumbNails.add(index, null);
    }

    public List<Image> getThumbnails() {
        return this.thumbNails;
    }

    public Image getThumbnailAt(int index) {
        return this.thumbNails.get(index);
    }

}
