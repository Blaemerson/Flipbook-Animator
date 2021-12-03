package main.java.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Layer extends Canvas {
    //imagine the GraphicsContext object as the 'pencil'
    //it can do much more than just lines, but it's what actually draws the shapes
    private final GraphicsContext gc;

    Layer(int width, int height){
        super(width, height);

        this.gc = this.getGraphicsContext2D();

        //setting width and color of line to be drawn
        gc.setLineWidth(1);
    }

    public void setPenColor(Color c) {
        this.gc.setStroke(c);
    }

    public void setPenSize(int size) {
        this.gc.setLineWidth(size);
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }
}