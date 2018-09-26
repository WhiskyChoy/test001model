package sample.entities;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import sample.utils.DrawUtil;

import java.util.ArrayList;

public class Trail {
    private ArrayList<Point> points = new ArrayList<>();
    private long id;
    private Canvas canvas;

    public Trail(ArrayList<Point> points, long id, int selectCounter) {
        this.points = points;
        this.id = id;
        this.selectCounter = selectCounter;
    }

    private int selectCounter = 0;

    public int getSelectCounter() {
        return selectCounter;
    }

    public void setSelectCounter(int selectCounter) {
        this.selectCounter = selectCounter;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Trail() {
        this.id = System.currentTimeMillis();
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public long getId() {
        return id;
    }

    public boolean isInRect(double x, double y, double width, double height) {
        for (Point point : points) {
            if (point.getX() < x || point.getY() < y || point.getX() > x + width || point.getY() > y + height) {
                return false;
            }
        }
        return true;
    }

    public void changeColor() {
        setColor(Color.RED);
    }

    public void initColor() {
        setColor(Color.BLACK);
    }

    private void setColor(Paint color) {
        if (canvas != null) {
            canvas.getGraphicsContext2D().setStroke(color);
            DrawUtil.clearCanvas(canvas);
            canvas.getGraphicsContext2D().stroke();
        }
    }

    public void add(Point point) {
        points.add(point);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trail) {
            return id == ((Trail) obj).getId();
        }
        return false;
    }
}
