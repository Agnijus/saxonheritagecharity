package com.example.saxonheritagecharity.model;

import org.bson.types.ObjectId;

public class Member {
    private ObjectId id;
    private String name;
    private String email;

    public Member(ObjectId id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
