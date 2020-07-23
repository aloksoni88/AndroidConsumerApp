package com.clicbrics.consumer.model;

import android.databinding.Bindable;

import java.util.List;
import java.util.SortedSet;

/**
 * Created by Alok on 03-08-2018.
 */
public class PropertyImageModel {

    private long projectId;
    private String projectName;
    private long area;
    private long maxArea;
    private long minPrice;
    private long maxPrice;
    private long minBsp;
    private long maxBsp;
    private boolean isOffer;
    private boolean isCommercial;
    private boolean isVirtualTour;
    private boolean isDriveView;
    private boolean isDroneView;
    private String projectCoverImage;
    private String address;
    private String status;
    private SortedSet<Integer> bedSet;
    private List<String> typeList;
    private String type;
    private boolean isAllSoldOut;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public long getArea() {
        return area;
    }

    public void setArea(long area) {
        this.area = area;
    }

    public long getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(long maxArea) {
        this.maxArea = maxArea;
    }

    public long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(long minPrice) {
        this.minPrice = minPrice;
    }

    public long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public long getMinBsp() {
        return minBsp;
    }

    public void setMinBsp(long minBsp) {
        this.minBsp = minBsp;
    }

    public long getMaxBsp() {
        return maxBsp;
    }

    public void setMaxBsp(long maxBsp) {
        this.maxBsp = maxBsp;
    }

    public boolean isOffer() {
        return isOffer;
    }

    public void setOffer(boolean offer) {
        isOffer = offer;
    }

    public boolean isCommercial() {
        return isCommercial;
    }

    public void setCommercial(boolean commercial) {
        isCommercial = commercial;
    }

    public boolean isVirtualTour() {
        return isVirtualTour;
    }

    public void setVirtualTour(boolean virtualTour) {
        isVirtualTour = virtualTour;
    }

    public boolean isDriveView() {
        return isDriveView;
    }

    public void setDriveView(boolean driveView) {
        isDriveView = driveView;
    }

    public boolean isDroneView() {
        return isDroneView;
    }

    public void setDroneView(boolean droneView) {
        isDroneView = droneView;
    }

    public String getProjectCoverImage() {
        return projectCoverImage;
    }

    public void setProjectCoverImage(String projectCoverImage) {
        this.projectCoverImage = projectCoverImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SortedSet<Integer> getBedSet() {
        return bedSet;
    }

    public void setBedSet(SortedSet<Integer> bedSet) {
        this.bedSet = bedSet;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAllSoldOut() {
        return isAllSoldOut;
    }

    public void setAllSoldOut(boolean allSoldOut) {
        isAllSoldOut = allSoldOut;
    }
}
