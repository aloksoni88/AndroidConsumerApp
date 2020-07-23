package com.clicbrics.consumer.model;


/**
 * Created by mehar on 15-09-2016.
 */

public class Amenity {

    private Long id;
    private String name;
    private String details;

    public Amenity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
