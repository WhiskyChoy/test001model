package sample.utils;

import sample.entities.Shape;
import sample.entities.Trail;

import java.util.ArrayList;
import java.util.HashSet;

public interface MessageGetter {
    ArrayList<Trail> getTrails();

    HashSet<Shape> getShapes();
}
