package com.onionskinstudio.flipbookanimator.model;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.LinkedList;


public class Thumbnail {

    Image thumbnailImage;

    SnapshotParameters params = new SnapshotParameters();


    public Thumbnail(Node frameToConvert) {

        thumbnailImage = frameToConvert.snapshot(params, null);
    }







    public Image getThumbnailImage() {
        return thumbnailImage;
    }

}
