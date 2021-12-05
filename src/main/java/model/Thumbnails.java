package model;

import javafx.scene.Node;

import java.util.LinkedList;
import java.util.List;

public class Thumbnails {

	
	private List<Thumbnail> thumbnails;
	
	//for when you need to make an empty thumbnail list, which is unlikely
	public Thumbnails() {
		
		thumbnails = new LinkedList<>();
		
	}
	
	
	
	//for when you're opening a file and need all the frames as thumbnails
	 public Thumbnails(List<Node> framesToConvert) {
		 
		 	Thumbnail t;
	        thumbnails = new LinkedList<>();
	        
	        for (Node f : framesToConvert) {
	        	t =  new Thumbnail(f);
	            thumbnails.add(t);
	        }
	    }
	 
	 
	 //for when you need to add a thumbnail
	 public void add(Thumbnail t) {
		 thumbnails.add(t);
	 }
	 
	 // remove the thumbnail for the frame specified by the index <frame>
	 public void remove(int frame) {
	        thumbnails.remove(frame);
	 }

	    // insert a thumbnail <thumb> at <index>, replacing what was previously there
	 public void insert(Thumbnail t, int index) {
	     if (thumbnails.size() > index) {
	            thumbnails.remove(index);
	     }
	        thumbnails.add(index, t);
	 }

	    // shift thumbnails to the right when adding a new frame
	 public void shiftThumbnails(int index) {
	        thumbnails.add(index, null);
	 }

	 public List<Thumbnail> getThumbnails() {
	        return this.thumbnails;
	 }

	 public Thumbnail getThumbnailAt(int index) {
	        return this.thumbnails.get(index);
	 }
	 
	 
	 
	 
}