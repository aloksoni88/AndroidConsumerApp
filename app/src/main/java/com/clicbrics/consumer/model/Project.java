package com.clicbrics.consumer.model;

import com.buy.housing.backend.propertyEndPoint.model.KeyValuePair;
import com.buy.housing.backend.propertyEndPoint.model.PaymentPlan;
import com.clicbrics.consumer.utils.Constants;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Project {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    @SerializedName("cityId")
    private long cityId;

    @SerializedName("builderId")
    private long builderId;

    @SerializedName("onBoardingDate")
    private Long onBoardingDate;

    @SerializedName("builderName")
    private String builderName;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("country")
    private String country;

    @SerializedName("pincode")
    private String pinCode;

    @SerializedName("launchDate")
    private long launchDate;

    @SerializedName("preLaunchDate")
    private long preLaunchDate;

    @SerializedName("possessionDate")
    private long possessionDate;

    @SerializedName("totalArea")
    private float totalArea;

    @SerializedName("greenArea")
    private Float greenArea;

    @SerializedName("areaUnit")
    private Constants.PropertyUnit areaUnit;

    @SerializedName("totalNumberBlock")
    private int totalNumberBlock;

    @SerializedName("blockList")
    private List<Long> blockList;

    @SerializedName("propertyTypeList")
    private List<Long> propertyTypeList;

    @SerializedName("numberofMultiStoryTowers")
    private int numberofMultiStoryTowers;

    @SerializedName("numberOfBuilderFloor")
    private int numberOfBuilderFloor;

    @SerializedName("numberOfVillas")
    private int numberOfVillas;

    @SerializedName("numberOfPlots")
    private int numberOfPlots;

    @SerializedName("nubmerOfShops")
    private int numberOfShops;

    @SerializedName("numberOfUnits")
    private int numberOfUnits;

    @SerializedName("priceRange")
    private String priceRange;

    @SerializedName("sizeRange")
    private String sizeRange;

    @SerializedName("propertyTypeRange")
    private String propertyTypeRange;

    @SerializedName("bspRange")
    private String bspRange;

    @SerializedName("propertyTypeJson")
    private String propertyTypeJson;

    @SerializedName("amenities")
    private List<Amenity> amenities;

    @SerializedName("Nearby")
    private List<Nearby> Nearby;

    @SerializedName("coverImage")
    private String coverImage;

    @SerializedName("sCoverImage")
    private String sCoverImage;

    @SerializedName("images")
    private List<Image>  images;

    @SerializedName("sitePlan")
    private FloorPlan sitePlan;

    @SerializedName("locationPlan")
    private FloorPlan locationPlan;

    @SerializedName("clusterPlan")
    private List<FloorPlan> clusterPlan;

    @SerializedName("amenityImages")
    private List<Image>  amenityImages;

    @SerializedName("neighbourImages")
    private List<Image>  neighbourImages;

    @SerializedName("constructionImages")
    private List<Image>  constructionImages;

    @SerializedName("otherImages")
    private String otherImages;

    @SerializedName("videos")
    private List<Video> videos;

    @SerializedName("bankLoan")
    private List<Bank> bankLoan;

    @SerializedName("projectType")
    private Constants.ProjectType projectType;

    @SerializedName("priceTrends")
    private List<PriceTrend> priceTrends;

    @SerializedName("newsUpdate")
    private String newsUpdate;

    @SerializedName("projectSummary")
    private String projectSummary;

    @SerializedName("locality")
    private String locality;

    @SerializedName("projectArchitect")
    private String projectArchitect;

    @SerializedName("hallipad")
    private String hallipad;

    @SerializedName("projectbrowcherPdfURL")
    private String projectbrowcherPdfURL;

    @SerializedName("FDI")
    private String FDI;

    @SerializedName("agentList")
    private List<Long> agentList; //Agent Id Only

    @SerializedName("livabilityScore")
    private Float livabilityScore;

    @SerializedName("connectivityScore")
    private Float connectivityScore;

    @SerializedName("safetyScore")
    private Float safetyScore;

    @SerializedName("valueForMoneyScore")
    private Float valueForMoneyScore;

    @SerializedName("lifeStyleScore")
    private Float lifeStyleScore;

    @SerializedName("searchNameInUp")
    private String searchNameInUp;

    @SerializedName("builderNameInUp")
    private String builderNameInUp;

    @SerializedName("paymentPlanList")
    private List<PaymentPlan> paymentPlanList;

    @SerializedName("logoImageURL")
    private String logoImageURL;

    @SerializedName("projectStatus")
    private Constants.AppConstants.PropertyStatus projectStatus;

    @SerializedName("walkThroughVideoURL")
    private String walkThroughVideoURL;

    @SerializedName("documents")
    private List<Document> documents;

    @SerializedName("plcList")
    private List<String> plcList;

    @SerializedName("plcDescriptionList")
    private List<KeyValuePair> plcDescriptionList;

    @SerializedName("noteList")
    private List<String> noteList;

    @SerializedName("RMAgentId")
    private Long RMAgentId;

    @SerializedName("RMAgentName")
    private String RMAgentName;

    @SerializedName("RMAgentPhone")
    private String RMAgentPhone;

    @SerializedName("developerIncentive")
    private String developerIncentive;

    @SerializedName("internalIncentive")
    private String internalIncentive;

    @SerializedName("startDate")
    private Long startDate;

    @SerializedName("endDate")
    private Long endDate;

    @SerializedName("targetAmount")
    private Long targetAmount;

    @SerializedName("mouURL")
    private String mouURL;

    @SerializedName("devContactList")
    private List<Contact> devContactList;

    @SerializedName("commisionSlabList")
    private List<CommisionSlab> commisionSlabList;

    @SerializedName("collectionSlabList")
    private List<CommisionSlab> collectionSlabList;

    @SerializedName("mouDocumentList")
    private List<Document> mouDocumentList;

    @SerializedName("commisionRemarks")
    private String commisionRemarks;

    @SerializedName("reraId")
    private String reraId;

    @SerializedName("tentative")
    private Boolean tentative;

    @SerializedName("offerAvailable")
    private Boolean offerAvailable;

    @SerializedName("offer")
    private String offer;

    @SerializedName("offerLink")
    private String offerLink;

    @SerializedName("startOfferDate")
    private Long startOfferDate;

    @SerializedName("endOfferDate")
    private Long endOfferDate;

    @SerializedName("sceoURL")
    private String sceoURL;

    @SerializedName("metaKey")
    private String metaKey;

    @SerializedName("metaTitle")
    private String metaTitle;

    @SerializedName("metaDescription")
    private String metaDescription;

    @SerializedName("exclusive")
    private Boolean exclusive;

    @SerializedName("expire")
    private boolean expire;

    @SerializedName("accNumber")
    private String accNumber;

    @SerializedName("ifsc")
    private String ifsc;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("accName")
    private String accName;

    @SerializedName("branchName")
    private String branchName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("htmlList")
    private List<KeyValuePair> htmlList;

    @SerializedName("bannerImages")
    private List<Image> bannerImages;

    @SerializedName("driveView")
    private DriveView driveView;

    @SerializedName("droneView")
    private DriveView droneView;

    private Boolean sold;

    private Boolean virtualTour;

    private Boolean commercial;

    private Long timeStamp;

    public Project() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public long getBuilderId() {
        return builderId;
    }

    public void setBuilderId(long builderId) {
        this.builderId = builderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(long launchDate) {
        this.launchDate = launchDate;
    }

    public long getPossessionDate() {
        return possessionDate;
    }

    public void setPossessionDate(long possessionDate) {
        this.possessionDate = possessionDate;
    }

    public float getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(float totalArea) {
        this.totalArea = totalArea;
    }

    public Float getGreenArea() {
        return greenArea;
    }

    public void setGreenArea(Float greenArea) {
        this.greenArea = greenArea;
    }

    public int getTotalNumberBlock() {
        return totalNumberBlock;
    }

    public void setTotalNumberBlock(int totalNumberBlock) {
        this.totalNumberBlock = totalNumberBlock;
    }

    public List<Long> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<Long> blockList) {
        this.blockList = blockList;
    }

    public List<Long> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<Long> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }

    public int getNumberofMultiStoryTowers() {
        return numberofMultiStoryTowers;
    }

    public void setNumberofMultiStoryTowers(int numberofMultiStoryTowers) {
        this.numberofMultiStoryTowers = numberofMultiStoryTowers;
    }

    public int getNumberOfBuilderFloor() {
        return numberOfBuilderFloor;
    }

    public void setNumberOfBuilderFloor(int numberOfBuilderFloor) {
        this.numberOfBuilderFloor = numberOfBuilderFloor;
    }

    public int getNumberOfVillas() {
        return numberOfVillas;
    }

    public void setNumberOfVillas(int numberOfVillas) {
        this.numberOfVillas = numberOfVillas;
    }

    public int getNumberOfPlots() {
        return numberOfPlots;
    }

    public void setNumberOfPlots(int numberOfPlots) {
        this.numberOfPlots = numberOfPlots;
    }

    public int getNumberOfShops() {
        return numberOfShops;
    }

    public void setNumberOfShops(int numberOfShops) {
        this.numberOfShops = numberOfShops;
    }

    public int getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(int numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getSizeRange() {
        return sizeRange;
    }

    public void setSizeRange(String sizeRange) {
        this.sizeRange = sizeRange;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public List<Nearby> getNearby() {
        return Nearby;
    }

    public void setNearby(List<Nearby> nearby) {
        Nearby = nearby;
    }


    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public FloorPlan getSitePlan() {
        return sitePlan;
    }

    public void setSitePlan(FloorPlan sitePlan) {
        this.sitePlan = sitePlan;
    }

    public FloorPlan getLocationPlan() {
        return locationPlan;
    }

    public void setLocationPlan(FloorPlan locationPlan) {
        this.locationPlan = locationPlan;
    }

    public List<FloorPlan> getClusterPlan() {
        return clusterPlan;
    }

    public void setClusterPlan(List<FloorPlan> clusterPlan) {
        this.clusterPlan = clusterPlan;
    }

    public List<Image> getAmenityImages() {
        return amenityImages;
    }

    public void setAmenityImages(List<Image> amenityImages) {
        this.amenityImages = amenityImages;
    }

    public List<Image> getNeighbourImages() {
        return neighbourImages;
    }

    public void setNeighbourImages(List<Image> neighbourImages) {
        this.neighbourImages = neighbourImages;
    }

    public List<Image> getConstructionImages() {
        return constructionImages;
    }

    public void setConstructionImages(List<Image> constructionImages) {
        this.constructionImages = constructionImages;
    }

    public String getOtherImages() {
        return otherImages;
    }

    public void setOtherImages(String otherImages) {
        this.otherImages = otherImages;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Bank> getBankLoan() {
        return bankLoan;
    }

    public void setBankLoan(List<Bank> bankLoan) {
        this.bankLoan = bankLoan;
    }

    public Constants.ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(Constants.ProjectType projectType) {
        this.projectType = projectType;
    }

    public List<PriceTrend> getPriceTrends() {
        return priceTrends;
    }

    public void setPriceTrends(List<PriceTrend> priceTrends) {
        this.priceTrends = priceTrends;
    }

    public String getNewsUpdate() {
        return newsUpdate;
    }

    public void setNewsUpdate(String newsUpdate) {
        this.newsUpdate = newsUpdate;
    }

    public String getProjectSummary() {
        return projectSummary;
    }

    public void setProjectSummary(String projectSummary) {
        this.projectSummary = projectSummary;
    }


    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getProjectArchitect() {
        return projectArchitect;
    }

    public void setProjectArchitect(String projectArchitect) {
        this.projectArchitect = projectArchitect;
    }

    public String getHallipad() {
        return hallipad;
    }

    public void setHallipad(String hallipad) {
        this.hallipad = hallipad;
    }

    public String getProjectbrowcherPdfURL() {
        return projectbrowcherPdfURL;
    }

    public void setProjectbrowcherPdfURL(String projectbrowcherPdfURL) {
        this.projectbrowcherPdfURL = projectbrowcherPdfURL;
    }

    public String getFDI() {
        return FDI;
    }

    public void setFDI(String FDI) {
        this.FDI = FDI;
    }

    public Float getLivabilityScore() {
        return livabilityScore;
    }

    public void setLivabilityScore(Float livabilityScore) {
        this.livabilityScore = livabilityScore;
    }

    public Float getConnectivityScore() {
        return connectivityScore;
    }

    public void setConnectivityScore(Float connectivityScore) {
        this.connectivityScore = connectivityScore;
    }

    public Float getSafetyScore() {
        return safetyScore;
    }

    public void setSafetyScore(Float safetyScore) {
        this.safetyScore = safetyScore;
    }



    public String getWalkThroughVideoURL() {
        return walkThroughVideoURL;
    }

    public void setWalkThroughVideoURL(String walkThroughVideoURL) {
        this.walkThroughVideoURL = walkThroughVideoURL;
    }

    public List<PaymentPlan> getPaymentPlanList() {
        return paymentPlanList;
    }

    public void setPaymentPlanList(List<PaymentPlan> paymentPlanList) {
        this.paymentPlanList = paymentPlanList;
    }

    public String getLogoImageURL() {
        return logoImageURL;
    }

    public void setLogoImageURL(String logoImageURL) {
        this.logoImageURL = logoImageURL;
    }

    public Constants.AppConstants.PropertyStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(Constants.AppConstants.PropertyStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public long getPreLaunchDate() {
        return preLaunchDate;
    }

    public void setPreLaunchDate(long preLaunchDate) {
        this.preLaunchDate = preLaunchDate;
    }

    public Constants.PropertyUnit getAreaUnit() {
        return areaUnit;
    }

    public void setAreaUnit(Constants.PropertyUnit areaUnit) {
        this.areaUnit = areaUnit;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getsCoverImage() {
        return sCoverImage;
    }

    public void setsCoverImage(String sCoverImage) {
        this.sCoverImage = sCoverImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Float getValueForMoneyScore() {
        return valueForMoneyScore;
    }

    public void setValueForMoneyScore(Float valueForMoneyScore) {
        this.valueForMoneyScore = valueForMoneyScore;
    }

    public Float getLifeStyleScore() {
        return lifeStyleScore;
    }

    public void setLifeStyleScore(Float lifeStyleScore) {
        this.lifeStyleScore = lifeStyleScore;
    }

    public List<Long> getAgentList() {
        return agentList;
    }

    public void setAgentList(List<Long> agentList) {
        this.agentList = agentList;
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public List<String> getPlcList() {
        return plcList;
    }

    public void setPlcList(List<String> plcList) {
        this.plcList = plcList;
    }

    public List<KeyValuePair> getPlcDescriptionList() {
        return plcDescriptionList;
    }

    public void setPlcDescriptionList(List<KeyValuePair> plcDescriptionList) {
        this.plcDescriptionList = plcDescriptionList;
    }

    public List<String> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<String> noteList) {
        this.noteList = noteList;
    }

    public String getSearchNameInUp() {
        return searchNameInUp;
    }

    public void setSearchNameInUp(String searchNameInUp) {
        this.searchNameInUp = searchNameInUp;
    }

    public String getBuilderNameInUp() {
        return builderNameInUp;
    }

    public void setBuilderNameInUp(String builderNameInUp) {
        this.builderNameInUp = builderNameInUp;
    }

    public Long getRMAgentId() {
        return RMAgentId;
    }

    public void setRMAgentId(Long RMAgentId) {
        this.RMAgentId = RMAgentId;
    }

    public String getRMAgentName() {
        return RMAgentName;
    }

    public void setRMAgentName(String RMAgentName) {
        this.RMAgentName = RMAgentName;
    }

    public String getRMAgentPhone() {
        return RMAgentPhone;
    }

    public void setRMAgentPhone(String RMAgentPhone) {
        this.RMAgentPhone = RMAgentPhone;
    }

    public String getDeveloperIncentive() {
        return developerIncentive;
    }

    public void setDeveloperIncentive(String developerIncentive) {
        this.developerIncentive = developerIncentive;
    }

    public String getInternalIncentive() {
        return internalIncentive;
    }

    public void setInternalIncentive(String internalIncentive) {
        this.internalIncentive = internalIncentive;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Long targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getMouURL() {
        return mouURL;
    }

    public void setMouURL(String mouURL) {
        this.mouURL = mouURL;
    }

    public List<Contact> getDevContactList() {
        return devContactList;
    }

    public void setDevContactList(List<Contact> devContactList) {
        this.devContactList = devContactList;
    }

    public List<CommisionSlab> getCommisionSlabList() {
        return commisionSlabList;
    }

    public void setCommisionSlabList(List<CommisionSlab> commisionSlabList) {
        this.commisionSlabList = commisionSlabList;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getOfferLink() {
        return offerLink;
    }

    public void setOfferLink(String offerLink) {
        this.offerLink = offerLink;
    }

    public Boolean getOfferAvailable() {
        return offerAvailable;
    }

    public void setOfferAvailable(Boolean offerAvailable) {
        this.offerAvailable = offerAvailable;
    }

    public Long getStartOfferDate() {
        return startOfferDate;
    }

    public void setStartOfferDate(Long startOfferDate) {
        this.startOfferDate = startOfferDate;
    }

    public Long getEndOfferDate() {
        return endOfferDate;
    }

    public void setEndOfferDate(Long endOfferDate) {
        this.endOfferDate = endOfferDate;
    }

    public Long getOnBoardingDate() {
        return onBoardingDate;
    }

    public void setOnBoardingDate(Long onBoardingDate) {
        this.onBoardingDate = onBoardingDate;
    }

    public String getSceoURL() {
        return sceoURL;
    }

    public void setSceoURL(String sceoURL) {
        this.sceoURL = sceoURL;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getReraId() {
        return reraId;
    }

    public void setReraId(String reraId) {
        this.reraId = reraId;
    }

    public Boolean getExclusive() {
        return exclusive;
    }

    public void setExclusive(Boolean exclusive) {
        this.exclusive = exclusive;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }


    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<CommisionSlab> getCollectionSlabList() {
        return collectionSlabList;
    }

    public void setCollectionSlabList(List<CommisionSlab> collectionSlabList) {
        this.collectionSlabList = collectionSlabList;
    }

    public List<Document> getMouDocumentList() {
        return mouDocumentList;
    }

    public void setMouDocumentList(List<Document> mouDocumentList) {
        this.mouDocumentList = mouDocumentList;
    }

    public Boolean getTentative() {
        return tentative;
    }

    public void setTentative(Boolean tentative) {
        this.tentative = tentative;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<KeyValuePair> getHtmlList() {
        return htmlList;
    }

    public void setHtmlList(List<KeyValuePair> htmlList) {
        this.htmlList = htmlList;
    }

    public List<Image> getBannerImages() {
        return bannerImages;
    }

    public void setBannerImages(List<Image> bannerImages) {
        this.bannerImages = bannerImages;
    }

    public String getCommisionRemarks() {
        return commisionRemarks;
    }

    public void setCommisionRemarks(String commisionRemarks) {
        this.commisionRemarks = commisionRemarks;
    }

    public boolean isExpire() {
        return expire;
    }

    public void setExpire(boolean expire) {
        this.expire = expire;
    }


    public String getPropertyTypeRange() {
        return propertyTypeRange;
    }

    public void setPropertyTypeRange(String propertyTypeRange) {
        this.propertyTypeRange = propertyTypeRange;
    }


    public String getBspRange() {
        return bspRange;
    }

    public void setBspRange(String bspRange) {
        this.bspRange = bspRange;
    }

    public String getPropertyTypeJson() {
        return propertyTypeJson;
    }

    public void setPropertyTypeJson(String propertyTypeJson) {
        this.propertyTypeJson = propertyTypeJson;
    }

    public DriveView getDriveView() {
        return driveView;
    }

    public void setDriveView(DriveView driveView) {
        this.driveView = driveView;
    }

    public DriveView getDroneView() {
        return droneView;
    }

    public void setDroneView(DriveView droneView) {
        this.droneView = droneView;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public Boolean getVirtualTour() {
        return virtualTour;
    }

    public void setVirtualTour(Boolean virtualTour) {
        this.virtualTour = virtualTour;
    }

    public Boolean getCommercial() {
        return commercial;
    }

    public void setCommercial(Boolean commercial) {
        this.commercial = commercial;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
