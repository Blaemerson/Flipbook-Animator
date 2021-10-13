import javafx.scene.canvas.*;
import javafx.scene.*;
import javafx.scene.paint.*;

public class Frame {
	
	private int width;
	private int height;
	private Canvas canvas;
	private GraphicsContext gc;
	
	Frame(int width, int height){
		this.width = width;
		this.height = height;
		
		this.canvas = new Canvas(width, height);
		
		this.gc = this.canvas.getGraphicsContext2D();

		gc.setLineWidth(1);
		
		canvas.setOnMousePressed(e->{
	            gc.setStroke(Color.BLACK);
	            gc.beginPath();
	            gc.lineTo(e.getX(), e.getY());
	        });
		
		canvas.setOnMouseDragged(e->{
            
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
		});
		
		
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
}
	
	
	
