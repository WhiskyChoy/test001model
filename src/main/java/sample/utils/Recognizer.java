package sample.utils;

import sample.context.DefaultShapeName;
import sample.entities.Shape;

public class Recognizer {
    public static String recognize(Shape shape){
        switch (shape.getNumOfTrail()){
            case 1: return DefaultShapeName.circle;
            case 2: return DefaultShapeName.triangle;
            case 3: return DefaultShapeName.rectangle;
            case 4: return DefaultShapeName.square;
            default: return DefaultShapeName.unknown;
        }
    }
}
