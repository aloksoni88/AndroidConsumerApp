/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2018-10-08 17:45:39 UTC)
 * on 2019-04-23 at 05:46:42 UTC 
 * Modify at your own risk.
 */

package com.buy.housing.backend.userEndPoint.model;

/**
 * Model definition for PropertyType.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class PropertyType extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String accessType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer area;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String assignedAgents;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float bookingPrice;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long bsp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer carpetArea;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer coveredBikeParking;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer coveredCarParking;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer doors;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Amenity> fittings;

  static {
    // hack to force ProGuard to consider Amenity used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Amenity.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Amenity> flatAmenities;

  static {
    // hack to force ProGuard to consider Amenity used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Amenity.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private FloorPlan floorPlan;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<FloorPlan> floorPlanList;

  static {
    // hack to force ProGuard to consider FloorPlan used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(FloorPlan.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Amenity> flooring;

  static {
    // hack to force ProGuard to consider Amenity used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Amenity.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String furnishedType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Image> images;

  static {
    // hack to force ProGuard to consider Image used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Image.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer kidsRoom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long launchDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberBathroom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfBalconies;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfBedrooms;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfFloors;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfGardens;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfPoojaRoom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfSarventRoom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfStoreRoom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfStudyRoom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfSwimmingPool;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer numberOfWaterTanks;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer openBikeParking;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer openCarParking;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String others;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String ownershipType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long possessionDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long projectId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String propertyDescription;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean soldStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String status;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer superArea;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer totalNoOfAvailableUnit;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer totalNumberOfUnits;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String type;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Video> videos;

  static {
    // hack to force ProGuard to consider Video used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Video.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Video virtualTour;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Amenity> walls;

  static {
    // hack to force ProGuard to consider Amenity used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Amenity.class);
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAccessType() {
    return accessType;
  }

  /**
   * @param accessType accessType or {@code null} for none
   */
  public PropertyType setAccessType(java.lang.String accessType) {
    this.accessType = accessType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getArea() {
    return area;
  }

  /**
   * @param area area or {@code null} for none
   */
  public PropertyType setArea(java.lang.Integer area) {
    this.area = area;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAssignedAgents() {
    return assignedAgents;
  }

  /**
   * @param assignedAgents assignedAgents or {@code null} for none
   */
  public PropertyType setAssignedAgents(java.lang.String assignedAgents) {
    this.assignedAgents = assignedAgents;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getBookingPrice() {
    return bookingPrice;
  }

  /**
   * @param bookingPrice bookingPrice or {@code null} for none
   */
  public PropertyType setBookingPrice(java.lang.Float bookingPrice) {
    this.bookingPrice = bookingPrice;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getBsp() {
    return bsp;
  }

  /**
   * @param bsp bsp or {@code null} for none
   */
  public PropertyType setBsp(java.lang.Long bsp) {
    this.bsp = bsp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getCarpetArea() {
    return carpetArea;
  }

  /**
   * @param carpetArea carpetArea or {@code null} for none
   */
  public PropertyType setCarpetArea(java.lang.Integer carpetArea) {
    this.carpetArea = carpetArea;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getCoveredBikeParking() {
    return coveredBikeParking;
  }

  /**
   * @param coveredBikeParking coveredBikeParking or {@code null} for none
   */
  public PropertyType setCoveredBikeParking(java.lang.Integer coveredBikeParking) {
    this.coveredBikeParking = coveredBikeParking;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getCoveredCarParking() {
    return coveredCarParking;
  }

  /**
   * @param coveredCarParking coveredCarParking or {@code null} for none
   */
  public PropertyType setCoveredCarParking(java.lang.Integer coveredCarParking) {
    this.coveredCarParking = coveredCarParking;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getDoors() {
    return doors;
  }

  /**
   * @param doors doors or {@code null} for none
   */
  public PropertyType setDoors(java.lang.Integer doors) {
    this.doors = doors;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Amenity> getFittings() {
    return fittings;
  }

  /**
   * @param fittings fittings or {@code null} for none
   */
  public PropertyType setFittings(java.util.List<Amenity> fittings) {
    this.fittings = fittings;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Amenity> getFlatAmenities() {
    return flatAmenities;
  }

  /**
   * @param flatAmenities flatAmenities or {@code null} for none
   */
  public PropertyType setFlatAmenities(java.util.List<Amenity> flatAmenities) {
    this.flatAmenities = flatAmenities;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public FloorPlan getFloorPlan() {
    return floorPlan;
  }

  /**
   * @param floorPlan floorPlan or {@code null} for none
   */
  public PropertyType setFloorPlan(FloorPlan floorPlan) {
    this.floorPlan = floorPlan;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<FloorPlan> getFloorPlanList() {
    return floorPlanList;
  }

  /**
   * @param floorPlanList floorPlanList or {@code null} for none
   */
  public PropertyType setFloorPlanList(java.util.List<FloorPlan> floorPlanList) {
    this.floorPlanList = floorPlanList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Amenity> getFlooring() {
    return flooring;
  }

  /**
   * @param flooring flooring or {@code null} for none
   */
  public PropertyType setFlooring(java.util.List<Amenity> flooring) {
    this.flooring = flooring;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFurnishedType() {
    return furnishedType;
  }

  /**
   * @param furnishedType furnishedType or {@code null} for none
   */
  public PropertyType setFurnishedType(java.lang.String furnishedType) {
    this.furnishedType = furnishedType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public PropertyType setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Image> getImages() {
    return images;
  }

  /**
   * @param images images or {@code null} for none
   */
  public PropertyType setImages(java.util.List<Image> images) {
    this.images = images;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getKidsRoom() {
    return kidsRoom;
  }

  /**
   * @param kidsRoom kidsRoom or {@code null} for none
   */
  public PropertyType setKidsRoom(java.lang.Integer kidsRoom) {
    this.kidsRoom = kidsRoom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getLaunchDate() {
    return launchDate;
  }

  /**
   * @param launchDate launchDate or {@code null} for none
   */
  public PropertyType setLaunchDate(java.lang.Long launchDate) {
    this.launchDate = launchDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public PropertyType setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberBathroom() {
    return numberBathroom;
  }

  /**
   * @param numberBathroom numberBathroom or {@code null} for none
   */
  public PropertyType setNumberBathroom(java.lang.Integer numberBathroom) {
    this.numberBathroom = numberBathroom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfBalconies() {
    return numberOfBalconies;
  }

  /**
   * @param numberOfBalconies numberOfBalconies or {@code null} for none
   */
  public PropertyType setNumberOfBalconies(java.lang.Integer numberOfBalconies) {
    this.numberOfBalconies = numberOfBalconies;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfBedrooms() {
    return numberOfBedrooms;
  }

  /**
   * @param numberOfBedrooms numberOfBedrooms or {@code null} for none
   */
  public PropertyType setNumberOfBedrooms(java.lang.Integer numberOfBedrooms) {
    this.numberOfBedrooms = numberOfBedrooms;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfFloors() {
    return numberOfFloors;
  }

  /**
   * @param numberOfFloors numberOfFloors or {@code null} for none
   */
  public PropertyType setNumberOfFloors(java.lang.Integer numberOfFloors) {
    this.numberOfFloors = numberOfFloors;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfGardens() {
    return numberOfGardens;
  }

  /**
   * @param numberOfGardens numberOfGardens or {@code null} for none
   */
  public PropertyType setNumberOfGardens(java.lang.Integer numberOfGardens) {
    this.numberOfGardens = numberOfGardens;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfPoojaRoom() {
    return numberOfPoojaRoom;
  }

  /**
   * @param numberOfPoojaRoom numberOfPoojaRoom or {@code null} for none
   */
  public PropertyType setNumberOfPoojaRoom(java.lang.Integer numberOfPoojaRoom) {
    this.numberOfPoojaRoom = numberOfPoojaRoom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfSarventRoom() {
    return numberOfSarventRoom;
  }

  /**
   * @param numberOfSarventRoom numberOfSarventRoom or {@code null} for none
   */
  public PropertyType setNumberOfSarventRoom(java.lang.Integer numberOfSarventRoom) {
    this.numberOfSarventRoom = numberOfSarventRoom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfStoreRoom() {
    return numberOfStoreRoom;
  }

  /**
   * @param numberOfStoreRoom numberOfStoreRoom or {@code null} for none
   */
  public PropertyType setNumberOfStoreRoom(java.lang.Integer numberOfStoreRoom) {
    this.numberOfStoreRoom = numberOfStoreRoom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfStudyRoom() {
    return numberOfStudyRoom;
  }

  /**
   * @param numberOfStudyRoom numberOfStudyRoom or {@code null} for none
   */
  public PropertyType setNumberOfStudyRoom(java.lang.Integer numberOfStudyRoom) {
    this.numberOfStudyRoom = numberOfStudyRoom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfSwimmingPool() {
    return numberOfSwimmingPool;
  }

  /**
   * @param numberOfSwimmingPool numberOfSwimmingPool or {@code null} for none
   */
  public PropertyType setNumberOfSwimmingPool(java.lang.Integer numberOfSwimmingPool) {
    this.numberOfSwimmingPool = numberOfSwimmingPool;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNumberOfWaterTanks() {
    return numberOfWaterTanks;
  }

  /**
   * @param numberOfWaterTanks numberOfWaterTanks or {@code null} for none
   */
  public PropertyType setNumberOfWaterTanks(java.lang.Integer numberOfWaterTanks) {
    this.numberOfWaterTanks = numberOfWaterTanks;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getOpenBikeParking() {
    return openBikeParking;
  }

  /**
   * @param openBikeParking openBikeParking or {@code null} for none
   */
  public PropertyType setOpenBikeParking(java.lang.Integer openBikeParking) {
    this.openBikeParking = openBikeParking;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getOpenCarParking() {
    return openCarParking;
  }

  /**
   * @param openCarParking openCarParking or {@code null} for none
   */
  public PropertyType setOpenCarParking(java.lang.Integer openCarParking) {
    this.openCarParking = openCarParking;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOthers() {
    return others;
  }

  /**
   * @param others others or {@code null} for none
   */
  public PropertyType setOthers(java.lang.String others) {
    this.others = others;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOwnershipType() {
    return ownershipType;
  }

  /**
   * @param ownershipType ownershipType or {@code null} for none
   */
  public PropertyType setOwnershipType(java.lang.String ownershipType) {
    this.ownershipType = ownershipType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getPossessionDate() {
    return possessionDate;
  }

  /**
   * @param possessionDate possessionDate or {@code null} for none
   */
  public PropertyType setPossessionDate(java.lang.Long possessionDate) {
    this.possessionDate = possessionDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getProjectId() {
    return projectId;
  }

  /**
   * @param projectId projectId or {@code null} for none
   */
  public PropertyType setProjectId(java.lang.Long projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPropertyDescription() {
    return propertyDescription;
  }

  /**
   * @param propertyDescription propertyDescription or {@code null} for none
   */
  public PropertyType setPropertyDescription(java.lang.String propertyDescription) {
    this.propertyDescription = propertyDescription;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getSoldStatus() {
    return soldStatus;
  }

  /**
   * @param soldStatus soldStatus or {@code null} for none
   */
  public PropertyType setSoldStatus(java.lang.Boolean soldStatus) {
    this.soldStatus = soldStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getStatus() {
    return status;
  }

  /**
   * @param status status or {@code null} for none
   */
  public PropertyType setStatus(java.lang.String status) {
    this.status = status;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getSuperArea() {
    return superArea;
  }

  /**
   * @param superArea superArea or {@code null} for none
   */
  public PropertyType setSuperArea(java.lang.Integer superArea) {
    this.superArea = superArea;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getTotalNoOfAvailableUnit() {
    return totalNoOfAvailableUnit;
  }

  /**
   * @param totalNoOfAvailableUnit totalNoOfAvailableUnit or {@code null} for none
   */
  public PropertyType setTotalNoOfAvailableUnit(java.lang.Integer totalNoOfAvailableUnit) {
    this.totalNoOfAvailableUnit = totalNoOfAvailableUnit;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getTotalNumberOfUnits() {
    return totalNumberOfUnits;
  }

  /**
   * @param totalNumberOfUnits totalNumberOfUnits or {@code null} for none
   */
  public PropertyType setTotalNumberOfUnits(java.lang.Integer totalNumberOfUnits) {
    this.totalNumberOfUnits = totalNumberOfUnits;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getType() {
    return type;
  }

  /**
   * @param type type or {@code null} for none
   */
  public PropertyType setType(java.lang.String type) {
    this.type = type;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Video> getVideos() {
    return videos;
  }

  /**
   * @param videos videos or {@code null} for none
   */
  public PropertyType setVideos(java.util.List<Video> videos) {
    this.videos = videos;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Video getVirtualTour() {
    return virtualTour;
  }

  /**
   * @param virtualTour virtualTour or {@code null} for none
   */
  public PropertyType setVirtualTour(Video virtualTour) {
    this.virtualTour = virtualTour;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Amenity> getWalls() {
    return walls;
  }

  /**
   * @param walls walls or {@code null} for none
   */
  public PropertyType setWalls(java.util.List<Amenity> walls) {
    this.walls = walls;
    return this;
  }

  @Override
  public PropertyType set(String fieldName, Object value) {
    return (PropertyType) super.set(fieldName, value);
  }

  @Override
  public PropertyType clone() {
    return (PropertyType) super.clone();
  }

}