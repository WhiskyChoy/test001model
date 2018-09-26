package sample.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sample.entities.Point;
import sample.entities.Trail;

import java.util.ArrayList;

public class DrawUtil {
    public static void drawOneTrail(GraphicsContext context, Trail trail) {
        ArrayList<Point> points = trail.getPoints();
        context.beginPath();
        context.moveTo(points.get(0).getX(), points.get(0).getY());
        for(int i = 1; i < points.size(); i++){
            Point point = points.get(i);
            context.lineTo(point.getX(),point.getY());
            context.stroke();
        }
    }

    public static void clearCanvas(Canvas canvas){
        canvas.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(),canvas.getHeight());
    }
}
