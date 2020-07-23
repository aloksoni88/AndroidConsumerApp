package com.clicbrics.consumer.retrofit.pojoclass;

public class Localities
{
    private String placeId;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    private String locality;

    public String getPlaceId ()
    {
        return placeId;
    }

    public void setPlaceId (String placeId)
    {
        this.placeId = placeId;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [placeId = "+placeId+", description = "+description+"]";
    }
}
