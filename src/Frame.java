import javafx.scene.canvas.*;
import javafx.scene.paint.*;

public class Frame {
	
	//will be useful for when we need to save a file
	private int width;
	private int height;
	
	//every frame has a canvas to draw on
	private Canvas canvas;
	
	//imagine the GraphicsContext object as the 'pencil'
	//it can do much more than just lines, but it's what actually draws the shapes
	private GraphicsContext gc;
	
	Frame(int width, int height){
		
		this.width = width;
		this.height = height;
		
		this.canvas = new Canvas(width, height);
		this.gc = this.canvas.getGraphicsContext2D();

		//setting width and color of line to be drawn
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		
		//this will probably have it's own event handler at some point
		//it's probably fine for a prototype though
		
		//events for when to draw
		canvas.setOnMousePressed(e->{
	            
	            gc.beginPath();
	            gc.lineTo(e.getX(), e.getY());
	        });
		
		//as it stands, something can only be drawn when the mouse moves
		//lineTo cannot make a line of length 0, so it makes sense
		canvas.setOnMouseDragged(e->{
            
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();   
		   });		
		
		
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
}
	
	
	
