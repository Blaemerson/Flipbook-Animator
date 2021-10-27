import javafx.scene.canvas.*;
import javafx.scene.paint.*;

public class Frame extends Canvas{
	
	
	
	//imagine the GraphicsContext object as the 'pencil'
	//it can do much more than just lines, but it's what actually draws the shapes
	private GraphicsContext gc;

	
	
	Frame(int width, int height){
		super(width, height);
	
		this.gc = this.getGraphicsContext2D();
		
		
		//setting width and color of line to be drawn
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		
		
		//this will have it's own event handler at some point
		//it's probably fine for a prototype though
		//events for when to draw
		this.setOnMousePressed(e->{
	            
	            gc.beginPath();
	            gc.lineTo(e.getX(), e.getY());
	        });
		
		//as it stands, something can only be drawn when the mouse moves
		//lineTo cannot make a line of length 0, so it makes sense
		this.setOnMouseDragged(e->{
            
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();   
                
               
		   });		
		
		
	}
	
	
	public GraphicsContext getGraphicsContext() {
		return gc;
	}
	
	
	
}
	
	
	
