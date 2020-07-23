package com.clicbrics.consumer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alok on 16-04-2019.
 */
public class ProjectListResponse {
    @SerializedName("count")
    private Integer count = null;
    @SerializedName("id")
    private Long id = null;
    @SerializedName("errorId")
    private Integer errorId = null;
    @SerializedName("errorMessage")
    private String errorMessage = null;
    @SerializedName("status")
    private Boolean status = null;
    @SerializedName("projectList")
    private List<Project> projectList = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }
}
