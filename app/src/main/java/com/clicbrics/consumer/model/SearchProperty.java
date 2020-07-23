package com.clicbrics.consumer.model;

import com.clicbrics.consumer.utils.Constants;

import java.util.List;
import java.util.SortedSet;

/**
 * Created by Alok on 30-07-2018.
 */
public class SearchProperty {

    private long property;
    private long projectId;
    private long cityId;
    private String projectName;
    private String builderName;
    private double lat;
    private double lng;
    private long possession;
    private String image;
    private String address;
    private String type;
    private long area;
    private long maxArea;
    private String furnished;
    private int bed;
    private int maxBed;
    private int bath;
    private long bsp;
    private long maxBsp;
    private String status;
    private int sponsor;
    private List<String> imageList;
    private boolean offer;
    private String sceoURL;
    private String imageResize;
    private float distanceInKM;
    private SortedSet<Integer> bedSet;
    private List<String> typeList;
    private List<Long> priceList;
    private long minPrice;
    private long maxPrice;
    private List<Integer> areaList;
    private List<Integer> bedroomList;
    private boolean isCommerical;
    private List<String> statusList;
    private long timeStamp;
    private String bhkType;
    private boolean isDriveView;
    private boolean isDroneView;
    private boolean isVirtualTour;
    private boolean isAllSoldOut;
    private int ourPicksScore;

    public SearchProperty(){

    }

    public SearchProperty(long projectId, String name, double lat, double lng, String address,
                          long area, long maxArea, int bed, int maxBeds, int bath, long bsp, long maxBsp,
                          String image, float distanceInKilometers, String status, String type, SortedSet<Integer> beds,
                          List<String> imageList, Long cityId, List<String> typeList,
                          String builderName, Boolean offer, List<Long> priceList, long minPrice, long maxPrice,
                          List<Integer> areaList, List<Integer> bedroomList, Boolean isCommercial, List<String> statusList,
                          boolean isVirtualTour, boolean isDriveView,boolean isDroneView,boolean isAllSoldOut,int ourPicksScore) {
        this.projectId = projectId;
        this.projectName = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.bed = bed;
        this.maxBed=maxBeds;
        this.bath = bath;
        this.bsp = bsp;
        this.maxBsp = maxBsp;
        this.image = image;
        this.distanceInKM = distanceInKilometers;
        this.status = status;
        this.type = type;
        //this.minPrice = (bsp * area);
        //this.maxPrice = (maxBsp * maxArea);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.bedSet = beds;
        this.imageList = imageList;
        this.cityId = cityId;
        this.typeList = typeList;
        this.builderName = builderName;
        this.offer = offer;
        this.priceList = priceList;
        this.areaList = areaList;
        this.bedroomList = bedroomList;
        this.isCommerical = isCommercial;
        this.statusList = statusList;
        this.isVirtualTour = isVirtualTour;
        this.isDriveView = isDriveView;
        this.isDroneView = isDroneView;
        this.isAllSoldOut = isAllSoldOut;
        this.ourPicksScore = ourPicksScore;
    }

    public SearchProperty(long projectId, String name, double lat, double lng, String address,
                          long area, long maxArea, int bed, int maxBeds, int bath, long bsp, long maxBsp,
                          String image, float distanceInKilometers, String status, String type, SortedSet<Integer> beds,
                          Long cityId, List<String> typeList,String builderName,Boolean offer, List<Long> priceList,
                          long minPrice, long maxPrice, List<Integer> areaList, List<Integer> bedroomList,
                          Boolean isCommercial,List<String> statusList, boolean isVirtualTour,
                          boolean isDriveView, boolean isDroneView,boolean isAllSoldOut,int ourPicksScore) {
        this.projectId = projectId;
        this.projectName = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.bed = bed;
        this.maxBed=maxBeds;
        this.bath = bath;
        this.bsp = bsp;
        this.maxBsp = maxBsp;
        this.image = image;
        this.distanceInKM = distanceInKilometers;
        this.status = status;
        this.type = type;
        //this.minPrice = (bsp * area);
        //this.maxPrice = (maxBsp * maxArea);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.bedSet = beds;
        this.cityId = cityId;
        this.typeList = typeList;
        this.builderName = builderName;
        this.offer = offer;
        this.priceList = priceList;
        this.areaList = areaList;
        this.bedroomList = bedroomList;
        this.isCommerical = isCommercial;
        this.statusList = statusList;
        this.isVirtualTour = isVirtualTour;
        this.isDriveView = isDriveView;
        this.isDroneView = isDroneView;
        this.isAllSoldOut = isAllSoldOut;
        this.ourPicksScore = ourPicksScore;
    }

    public SearchProperty(long projectId,String projectName,String builderName,long minPrice,long maxPrice,
            SortedSet<Integer> bedSet,String type,List<String> typeList,long area,long maxArea,String address,String status,long cityId,
            boolean isCommerical,boolean offer,String image,List<String> imageList,long timeStamp,
                          String bhkType,boolean isVirtualTour,List<String> statusList,
                          boolean isAllSoldOut,boolean isDriveView, boolean isDroneView){
        this.projectId = projectId;
        this.projectName = projectName;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.image = image;
        this.status = status;
        this.type = type;
        this.typeList = typeList;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.bedSet = bedSet;
        this.cityId = cityId;
        this.builderName = builderName;
        this.offer = offer;
        this.isCommerical = isCommerical;
        this.imageList = imageList;
        this.timeStamp = timeStamp;
        this.bhkType = bhkType;
        this.isVirtualTour = isVirtualTour;
        this.isDriveView = isDriveView;
        this.isDroneView = isDroneView;
        this.statusList = statusList;
        this.isAllSoldOut = isAllSoldOut;
    }

    public long getProperty() {
        return property;
    }

    public void setProperty(long property) {
        this.property = property;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String name) {
        this.projectName = name;
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getPossession() {
        return possession;
    }

    public void setPossession(long possession) {
        this.possession = possession;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getFurnished() {
        return furnished;
    }

    public void setFurnished(String furnished) {
        this.furnished = furnished;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }

    public int getMaxBed() {
        return maxBed;
    }

    public void setMaxBed(int maxBed) {
        this.maxBed = maxBed;
    }

    public int getBath() {
        return bath;
    }

    public void setBath(int bath) {
        this.bath = bath;
    }

    public long getBsp() {
        return bsp;
    }

    public void setBsp(long bsp) {
        this.bsp = bsp;
    }

    public long getMaxBsp() {
        return maxBsp;
    }

    public void setMaxBsp(long maxBsp) {
        this.maxBsp = maxBsp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSponsor() {
        return sponsor;
    }

    public void setSponsor(int sponsor) {
        this.sponsor = sponsor;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public boolean isOffer() {
        return offer;
    }

    public void setOffer(boolean offer) {
        this.offer = offer;
    }

    public String getSceoURL() {
        return sceoURL;
    }

    public void setSceoURL(String sceoURL) {
        this.sceoURL = sceoURL;
    }

    public String getImageResize() {
        return imageResize;
    }

    public void setImageResize(String imageResize) {
        this.imageResize = imageResize;
    }

    public float getDistanceInKM() {
        return distanceInKM;
    }

    public void setDistanceInKM(float distanceInKM) {
        this.distanceInKM = distanceInKM;
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

    public List<Long> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<Long> priceList) {
        this.priceList = priceList;
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

    public List<Integer> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Integer> areaList) {
        this.areaList = areaList;
    }

    public List<Integer> getBedroomList() {
        return bedroomList;
    }

    public void setBedroomList(List<Integer> bedroomList) {
        this.bedroomList = bedroomList;
    }

    public boolean isCommerical() {
        return isCommerical;
    }

    public void setCommerical(boolean commerical) {
        isCommerical = commerical;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBhkType() {
        return bhkType;
    }

    public void setBhkType(String bhkType) {
        this.bhkType = bhkType;
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

    public boolean isAllSoldOut() {
        return isAllSoldOut;
    }

    public void setAllSoldOut(boolean allSoldOut) {
        isAllSoldOut = allSoldOut;
    }

    public int getOurPicksScore() {
        return ourPicksScore;
    }

    public void setOurPicksScore(int ourPicksScore) {
        this.ourPicksScore = ourPicksScore;
    }
}
