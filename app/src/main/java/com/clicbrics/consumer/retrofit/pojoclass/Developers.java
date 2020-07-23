package com.clicbrics.consumer.retrofit.pojoclass;

public class Developers
{
    private String builderId;

    private String builderName;

    public String getBuilderId ()
    {
        return builderId;
    }

    public void setBuilderId (String builderId)
    {
        this.builderId = builderId;
    }

    public String getBuilderName ()
    {
        return builderName;
    }

    public void setBuilderName (String builderName)
    {
        this.builderName = builderName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [builderId = "+builderId+", builderName = "+builderName+"]";
    }
}