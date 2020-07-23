package com.clicbrics.consumer.model;


/**
 * Created by Ranjeet Jha on 02-02-2017.
 */
public class CommisionSlab {

    private Long id;
    private int startRange;
    private int endRange;
    private float commision;
    private float additional;
    private Float collection;
    private String remraks;


    public CommisionSlab() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStartRange() {
        return startRange;
    }

    public void setStartRange(int startRange) {
        this.startRange = startRange;
    }

    public int getEndRange() {
        return endRange;
    }

    public void setEndRange(int endRange) {
        this.endRange = endRange;
    }

    public float getCommision() {
        return commision;
    }

    public void setCommision(float commision) {
        this.commision = commision;
    }

    public float getAdditional() {
        return additional;
    }

    public void setAdditional(float additional) {
        this.additional = additional;
    }

    public Float getCollection() {
        return collection;
    }

    public void setCollection(Float collection) {
        this.collection = collection;
    }
}
