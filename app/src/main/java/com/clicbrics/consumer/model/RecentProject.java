package com.clicbrics.consumer.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.clicbrics.consumer.BR;

import java.util.List;

/**
 * Created by Alok on 31-07-2018.
 */
public class RecentProject extends BaseObservable {

    private long projectId;
    private String projectName;
    private String developerName;
    private String priceRange;
    private String bedroomRange;
    private String propertyType;
    private String areaRange;
    private String address;
    private String status;
    private String city;
    private long cityId;
    private boolean isCommercial;
    private boolean isOfferExist;
    private String projectImageURL;
    private List<String> imageList;
    private long timeStamp;
    private String bhkType;

    public RecentProject(){

    }

    public RecentProject(long projectId, String projectName, String developerName, String priceRange,
                         String bedroomRange, String propertyType, String areaRange, String address,
                         String status, String city, long cityId, boolean isCommercial, boolean isOfferExist,
                         String projectImageURL, List<String> imageList, long timeStamp) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.developerName = developerName;
        this.priceRange = priceRange;
        this.bedroomRange = bedroomRange;
        this.propertyType = propertyType;
        this.areaRange = areaRange;
        this.address = address;
        this.status = status;
        this.city = city;
        this.cityId = cityId;
        this.isCommercial = isCommercial;
        this.isOfferExist = isOfferExist;
        this.projectImageURL = projectImageURL;
        this.imageList = imageList;
        this.timeStamp = timeStamp;
    }

    @Bindable
    public long getProjectId() {
        return projectId;
    }

    @Bindable
    public void setProjectId(long projectId) {
        this.projectId = projectId;
        notifyPropertyChanged(BR.projectId);
    }

    @Bindable
    public String getProjectName() {
        return projectName;
    }

    @Bindable
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    @Bindable
    public String getDeveloperName() {
        return developerName;
    }
    @Bindable
    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }
    @Bindable
    public String getPriceRange() {
        return priceRange;
    }
    @Bindable
    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }
    @Bindable
    public String getBedroomRange() {
        return bedroomRange;
    }
    @Bindable
    public void setBedroomRange(String bedroomRange) {
        this.bedroomRange = bedroomRange;
    }
    @Bindable
    public String getPropertyType() {
        return propertyType;
    }
    @Bindable
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
    @Bindable
    public String getAreaRange() {
        return areaRange;
    }
    @Bindable
    public void setAreaRange(String areaRange) {
        this.areaRange = areaRange;
    }
    @Bindable
    public String getAddress() {
        return address;
    }
    @Bindable
    public void setAddress(String address) {
        this.address = address;
    }
    @Bindable
    public String getStatus() {
        return status;
    }
    @Bindable
    public void setStatus(String status) {
        this.status = status;
    }
    @Bindable
    public String getCity() {
        return city;
    }
    @Bindable
    public void setCity(String city) {
        this.city = city;
    }
    @Bindable
    public long getCityId() {
        return cityId;
    }
    @Bindable
    public void setCityId(long cityId) {
        this.cityId = cityId;
    }
    @Bindable
    public boolean isCommercial() {
        return isCommercial;
    }
    @Bindable
    public void setCommercial(boolean commercial) {
        isCommercial = commercial;
    }
    @Bindable
    public boolean isOfferExist() {
        return isOfferExist;
    }
    @Bindable
    public void setOfferExist(boolean offerExist) {
        isOfferExist = offerExist;
    }
    @Bindable
    public String getProjectImageURL() {
        return projectImageURL;
    }
    @Bindable
    public void setProjectImageURL(String projectImageURL) {
        this.projectImageURL = projectImageURL;
    }
    @Bindable
    public List<String> getImageList() {
        return imageList;
    }
    @Bindable
    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
    @Bindable
    public long getTimeStamp() {
        return timeStamp;
    }
    @Bindable
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBhkType() {
        return bhkType;
    }

    public void setBhkType(String bhkType) {
        this.bhkType = bhkType;
    }
}
