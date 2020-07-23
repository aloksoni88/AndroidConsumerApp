package com.clicbrics.consumer.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

import com.buy.housing.backend.propertyEndPoint.model.ProjectScore;

import java.util.List;
import java.util.SortedSet;


/**
 * Created by Paras on 21-09-2016.
 * Class responsiable for handling Property Grouping
 */
public class ProjectWrapper implements Parcelable {
    private long projectId;
    private String name;
    private double lat;
    private double lng;
    private String address;
    private long area;
    private long maxArea;
    private int bed;
    private int maxBeds;
    private int bath;
    private long bsp;
    private long maxBsp;
    private String image;
    private float distanceInKilometers;
    private String status;
    private long minPrice;
    private long maxPrice;
    private long score;
    public boolean isClicked;
    private List<String> imageList;

    private String type;
    private SortedSet<Integer> bedList;
    private long cityId;
    private ProjectScore projectScore;
    private long timeStamp;
    private List<String> typeList;
    private String builderName;
    private boolean offer;
    private List<Long> priceList;
    private List<Integer> areaList;
    private List<Integer> bedroomList;
    private boolean isCommercial;
    private List<String> statusList;

    //private long offerStartDate;
    //private long offerEndDate;

    public List<Integer> getBedroomList() {
        return bedroomList;
    }

    public void setBedroomList(List<Integer> bedroomList) {
        this.bedroomList = bedroomList;
    }

    public SortedSet<Integer> getBedList() {
        return bedList;
    }

    public void setBedList(SortedSet<Integer> bedList) {
        this.bedList = bedList;
    }

    public long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(long minPrice) {
        this.minPrice = minPrice;
    }

    public List<Long> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<Long> priceList) {
        this.priceList = priceList;
    }

    public List<Integer> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Integer> areaList) {
        this.areaList = areaList;
    }

    protected ProjectWrapper(Parcel in) {
        projectId = in.readLong();
        name = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        area = in.readLong();
        maxArea = in.readLong();
        bed = in.readInt();
        maxBeds = in.readInt();
        bath = in.readInt();
        bsp = in.readLong();
        maxBsp = in.readLong();
        image = in.readString();
        distanceInKilometers = (float)in.readDouble();
        status = in.readString();
        minPrice = in.readLong();
        maxPrice = in.readLong();
        score = in.readLong();
        isClicked = in.readByte() != 0;
        imageList = in.createStringArrayList();
        type = in.readString();
        timeStamp = in.readLong();
    }

    public static final Creator<ProjectWrapper> CREATOR = new Creator<ProjectWrapper>() {
        @Override
        public ProjectWrapper createFromParcel(Parcel in) {
            return new ProjectWrapper(in);
        }

        @Override
        public ProjectWrapper[] newArray(int size) {
            return new ProjectWrapper[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(projectId);
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(address);
        dest.writeLong(area);
        dest.writeLong(maxArea);
        dest.writeInt(bed);
        dest.writeInt(maxBeds);
        dest.writeInt(bath);
        dest.writeLong(bsp);
        dest.writeLong(maxBsp);
        dest.writeString(image);
        dest.writeDouble(distanceInKilometers);
        dest.writeString(status);
        dest.writeLong(minPrice);
        dest.writeLong(maxPrice);
        dest.writeLong(score);
        dest.writeList(imageList);
    }

    public ProjectWrapper(long projectId, String name, String image, long minPrice, long cityId,
                          long timeStamp,boolean isCommercial, boolean isOffer) {
        this.projectId = projectId;
        this.name = name;
        this.image = image;
        this.minPrice = minPrice;
        this.timeStamp = timeStamp;
        this.cityId = cityId;
        this.isCommercial = isCommercial;
        this.offer = isOffer;
    }

    public ProjectWrapper(long projectId, String name, double lat, double lng, String address,
                          long area, int bed, int bath, long bsp, String image,
                          float distanceInKilometers, String status, String type,SortedSet<Integer> beds) {
        this.projectId = projectId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.bed = bed;
        this.bath = bath;
        this.bsp = bsp;
        this.image = image;
        this.distanceInKilometers = distanceInKilometers;
        this.status = status;
        this.type = type;
        this.minPrice = (bsp * area);
        this.bedList = beds;
    }

    public ProjectWrapper(long projectId, String name, double lat, double lng, String address,
                          long area, long maxArea, int bed, int maxBeds, int bath, long bsp, long maxBsp,
                          String image, float distanceInKilometers, String status, String type,SortedSet<Integer> beds) {
        this.projectId = projectId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.bed = bed;
        this.maxBeds=maxBeds;
        this.bath = bath;
        this.bsp = bsp;
        this.maxBsp = maxBsp;
        this.image = image;
        this.distanceInKilometers = distanceInKilometers;
        this.status = status;
        this.type = type;
        this.minPrice = (bsp * area);
        this.maxPrice = (maxBsp * maxArea);
        this.bedList = beds;
    }

    public ProjectWrapper(long projectId, String name, double lat, double lng, String address,
                          long area, long maxArea, int bed, int maxBeds, int bath, long bsp, long maxBsp,
                          String image, float distanceInKilometers, String status, String type, SortedSet<Integer> beds,
                          List<String> imageList) {
        this.projectId = projectId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.bed = bed;
        this.maxBeds=maxBeds;
        this.bath = bath;
        this.bsp = bsp;
        this.maxBsp = maxBsp;
        this.image = image;
        this.distanceInKilometers = distanceInKilometers;
        this.status = status;
        this.type = type;
        this.minPrice = (bsp * area);
        this.maxPrice = (maxBsp * maxArea);
        this.bedList = beds;
        this.imageList = imageList;
    }

    public ProjectWrapper(long projectId, String name, double lat, double lng, String address,
                          long area, long maxArea, int bed, int maxBeds, int bath, long bsp, long maxBsp,
                          String image, float distanceInKilometers, String status, String type, SortedSet<Integer> beds,
                          List<String> imageList, Long cityId, List<String> typeList,
                          String builderName, Boolean offer, List<Long> priceList, long minPrice, long maxPrice,
                          List<Integer> areaList, List<Integer> bedroomList, Boolean isCommercial, List<String> statusList) {
        this.projectId = projectId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.bed = bed;
        this.maxBeds=maxBeds;
        this.bath = bath;
        this.bsp = bsp;
        this.maxBsp = maxBsp;
        this.image = image;
        this.distanceInKilometers = distanceInKilometers;
        this.status = status;
        this.type = type;
        //this.minPrice = (bsp * area);
        //this.maxPrice = (maxBsp * maxArea);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.bedList = beds;
        this.imageList = imageList;
        this.cityId = cityId;
        this.typeList = typeList;
        this.builderName = builderName;
        this.offer = offer;
        this.priceList = priceList;
        this.areaList = areaList;
        this.bedroomList = bedroomList;
        this.isCommercial = isCommercial;
        this.statusList = statusList;
    }

    public ProjectWrapper(long projectId, String name, double lat, double lng, String address,
                          long area, long maxArea, int bed, int maxBeds, int bath, long bsp, long maxBsp,
                          String image, float distanceInKilometers, String status, String type, SortedSet<Integer> beds,
                          Long cityId, List<String> typeList,String builderName,Boolean offer, List<Long> priceList,
                          long minPrice, long maxPrice, List<Integer> areaList, List<Integer> bedroomList,
                          Boolean isCommercial,List<String> statusList) {
        this.projectId = projectId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.bed = bed;
        this.maxBeds=maxBeds;
        this.bath = bath;
        this.bsp = bsp;
        this.maxBsp = maxBsp;
        this.image = image;
        this.distanceInKilometers = distanceInKilometers;
        this.status = status;
        this.type = type;
        //this.minPrice = (bsp * area);
        //this.maxPrice = (maxBsp * maxArea);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.bedList = beds;
        this.cityId = cityId;
        this.typeList = typeList;
        this.builderName = builderName;
        this.offer = offer;
        this.priceList = priceList;
        this.areaList = areaList;
        this.bedroomList = bedroomList;
        this.isCommercial = isCommercial;
        this.statusList = statusList;
    }

    public ProjectWrapper(long projectId, String name, double lat, double lng, String address,
                          long area, long maxArea, int bed, int maxBeds, int bath, long bsp, long maxBsp,
                          String image, float distanceInKilometers, String status, String type, SortedSet<Integer> beds,
                          Long cityId, ProjectScore projectScore, Boolean offer, Boolean isCommercial) {
        this.projectId = projectId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.area = area;
        this.maxArea = maxArea;
        this.bed = bed;
        this.maxBeds=maxBeds;
        this.bath = bath;
        this.bsp = bsp;
        this.maxBsp = maxBsp;
        this.image = image;
        this.distanceInKilometers = distanceInKilometers;
        this.status = status;
        this.type = type;
        this.minPrice = (bsp * area);
        this.maxPrice = (maxBsp * maxArea);
        this.bedList = beds;
        this.cityId = cityId;
        this.projectScore = projectScore;
        this.offer = offer;
        this.isCommercial = isCommercial;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDistanceInKilometers() {
        return distanceInKilometers;
    }

    public void setDistanceInKilometers(float distanceInKilometers) {
        this.distanceInKilometers = distanceInKilometers;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getArea() {
        return area;
    }

    public void setArea(long area) {
        this.area = area;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
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

    public void setMaxPrice(long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public long getMaxPrice() {
        return maxPrice;
    }

    public int getMaxBeds() {
        return maxBeds;
    }

    public long getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(long maxarea) {
        this.maxArea = maxarea;
    }

    public long getMaxBsp() {
        return maxBsp;
    }

    public void setMaxBsp(long maxBsp) {
        this.maxBsp = maxBsp;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public ProjectScore getProjectScore() {
        return projectScore;
    }

    public void setProjectScore(ProjectScore projectScore) {
        this.projectScore = projectScore;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public boolean isOffer() {
        return offer;
    }

    public void setOffer(boolean offer) {
        this.offer = offer;
    }

    public boolean isCommercial() {
        return isCommercial;
    }

    public void setCommercial(boolean commercial) {
        isCommercial = commercial;
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


    public void setMaxBeds(int maxBeds) {
        this.maxBeds = maxBeds;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
