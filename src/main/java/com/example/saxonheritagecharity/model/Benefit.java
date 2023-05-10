package com.example.saxonheritagecharity.model;
import org.bson.types.ObjectId;

public class Benefit {
    private ObjectId id;
    private String name;

    public Benefit(ObjectId id, String name) {
        this.id = id;
        this.name = name;
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
