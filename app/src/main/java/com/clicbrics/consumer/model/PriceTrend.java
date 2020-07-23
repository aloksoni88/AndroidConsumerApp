package com.clicbrics.consumer.model;


/**
 * Created by mehar on 15-09-2016.
 */
public class PriceTrend {

    private Long id;
    private Long time;
    private Float price;
    private String unit;

    public PriceTrend() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
