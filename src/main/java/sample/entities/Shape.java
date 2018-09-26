package sample.entities;

import java.util.HashSet;

public class Shape {
    private HashSet<Trail> trails = new HashSet<>();
    private long id = System.currentTimeMillis();
    private String mark = "";

    public Shape(HashSet<Trail> trails, long id, String mark) {
        this.trails = trails;
        this.id = id;
        this.mark = mark;
    }

    public long getId() {
        return id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getNumOfTrail(){
        return trails.size();
    }

    public Shape() {
    }

    public HashSet<Trail> getTrails() {
        return trails;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Shape) {
            return trails.equals(((Shape) obj).trails);
        }
        return false;
    }

    public void add(Trail trail){
        trails.add(trail);
    }
}
