import javafx.scene.canvas.*;
import javafx.scene.paint.*;

public class Frame {
	
	private int width;
	private int height;
	
	
	//every frame has a canvas to draw on
	private Canvas canvas;
	
	//imagine the GraphicsContext object as the 'pencil'
	//it can do much more than just lines, but it's what actually draws the shapes
	private GraphicsContext gc;
	
	Frame(int width, int height){
		
		this.setWidth(width);
		this.setHeight(height);
		
		this.canvas = new Canvas(width, height);
		this.gc = this.canvas.getGraphicsContext2D();
		
		setBackground(Color.WHITE);
		
		//setting width and color of line to be drawn
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		
		
		//this will have it's own event handler at some point
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
	
	public void setBackground(Color c) {
		gc.setFill(c);
		gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
	}
	
	public Canvas getCanvas() {
		return canvas;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
}
	
	
	
