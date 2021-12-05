package model;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;


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