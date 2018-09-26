package sample.utils;

import sample.entities.Shape;
import sample.entities.Trail;

import java.util.ArrayList;
import java.util.HashSet;

public class MessageWrapper implements MessageGetter{
    private ArrayList<Trail> trails;
    private HashSet<Shape> shapes;

    @Override
    public ArrayList<Trail> getTrails() {
        return trails;
    }

    @Override
    public HashSet<Shape> getShapes() {
        return shapes;
    }

    MessageWrapper(ArrayList<Trail> trails, HashSet<Shape> shapes) {
        this.trails = trails;
        this.shapes = shapes;
    }
}
