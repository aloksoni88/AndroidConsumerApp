package com.clicbrics.consumer.retrofit.pojoclass;

public class Projects
{
    private String sceoURL;

    private String projectName;

    private String projectId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public String getSceoURL ()
    {
        return sceoURL;
    }

    public void setSceoURL (String sceoURL)
    {
        this.sceoURL = sceoURL;
    }

    public String getProjectName ()
    {
        return projectName;
    }

    public void setProjectName (String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectId ()
    {
        return projectId;
    }

    public void setProjectId (String projectId)
    {
        this.projectId = projectId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [sceoURL = "+sceoURL+", projectName = "+projectName+", projectId = "+projectId+"]";
    }
}
