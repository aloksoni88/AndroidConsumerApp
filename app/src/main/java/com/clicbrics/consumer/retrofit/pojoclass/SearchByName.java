package com.clicbrics.consumer.retrofit.pojoclass;

import com.buy.housing.backend.loanEndPoint.model.Project;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchByName {
   /* private Localities[] localities;

    private Projects[] projects;

    private Developers[] developers;*/
   @SerializedName("developers")
    private List<Developers> developers;
    @SerializedName("projects")
    private List<Projects> projects;
    @SerializedName("localities")
    private List<Localities> localities;
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("id")
    private Long id;
    @SerializedName("errorId")
    private String errorId;
    @SerializedName("seq")
    private String seq;
    @SerializedName("status")
    private Boolean status;


    public List<Projects> getProjects() {
        return projects;
    }

    public void setProjectList(List<Projects> projects) {
        this.projects = projects;
    }



    public void setLocalities (List<Localities> localities)
    {
        this.localities = localities;
    }

    public List<Localities> getLocalities ()
    {
        return localities;
    }



    public List<Developers> getDevelopers ()
    {
        return developers;
    }

    public void setDevelopers (List<Developers> developers)
    {
        this.developers = developers;
    }

    public String getErrorMessage ()
    {
        return errorMessage;
    }

    public void setErrorMessage (String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public Long getId ()
    {
        return id;
    }

    public void setId (Long id)
    {
        this.id = id;
    }

    public String getErrorId ()
    {
        return errorId;
    }

    public void setErrorId (String errorId)
    {
        this.errorId = errorId;
    }

    public String getSeq ()
    {
        return seq;
    }

    public void setSeq (String seq)
    {
        this.seq = seq;
    }

    public Boolean getStatus ()
    {
        return status;
    }

    public void setStatus (Boolean status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [localities = "+localities.toString()+", projects = "+projects.toString()+", developers = "+developers.toString()+", errorMessage = "+errorMessage+", id = "+id+", errorId = "+errorId+", seq = "+seq+", status = "+status+"]";
    }
}
