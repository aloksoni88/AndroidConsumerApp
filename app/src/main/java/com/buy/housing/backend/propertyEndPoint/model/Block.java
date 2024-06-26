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
 * on 2019-11-29 at 11:12:27 UTC 
 * Modify at your own risk.
 */

package com.buy.housing.backend.propertyEndPoint.model;

/**
 * Model definition for Block.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Block extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Amenity> amenities;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String blockType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<FloorPlan> floorPlan;

  static {
    // hack to force ProGuard to consider FloorPlan used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(FloorPlan.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean hallipad;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String kmlurl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Double lat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Double lng;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer numberOfFloors;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer numberOfLifts;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer numberOfPenthouse;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer numberOfUnits;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer numberOfUnitsPerFloor;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String others;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long projectId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<Long> propertyUnits;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String towerStatus;

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Amenity> getAmenities() {
    return amenities;
  }

  /**
   * @param amenities amenities or {@code null} for none
   */
  public Block setAmenities(java.util.List<Amenity> amenities) {
    this.amenities = amenities;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getBlockType() {
    return blockType;
  }

  /**
   * @param blockType blockType or {@code null} for none
   */
  public Block setBlockType(String blockType) {
    this.blockType = blockType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<FloorPlan> getFloorPlan() {
    return floorPlan;
  }

  /**
   * @param floorPlan floorPlan or {@code null} for none
   */
  public Block setFloorPlan(java.util.List<FloorPlan> floorPlan) {
    this.floorPlan = floorPlan;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getHallipad() {
    return hallipad;
  }

  /**
   * @param hallipad hallipad or {@code null} for none
   */
  public Block setHallipad(Boolean hallipad) {
    this.hallipad = hallipad;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Block setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getKmlurl() {
    return kmlurl;
  }

  /**
   * @param kmlurl kmlurl or {@code null} for none
   */
  public Block setKmlurl(String kmlurl) {
    this.kmlurl = kmlurl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Double getLat() {
    return lat;
  }

  /**
   * @param lat lat or {@code null} for none
   */
  public Block setLat(Double lat) {
    this.lat = lat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Double getLng() {
    return lng;
  }

  /**
   * @param lng lng or {@code null} for none
   */
  public Block setLng(Double lng) {
    this.lng = lng;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public Block setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getNumberOfFloors() {
    return numberOfFloors;
  }

  /**
   * @param numberOfFloors numberOfFloors or {@code null} for none
   */
  public Block setNumberOfFloors(Integer numberOfFloors) {
    this.numberOfFloors = numberOfFloors;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getNumberOfLifts() {
    return numberOfLifts;
  }

  /**
   * @param numberOfLifts numberOfLifts or {@code null} for none
   */
  public Block setNumberOfLifts(Integer numberOfLifts) {
    this.numberOfLifts = numberOfLifts;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getNumberOfPenthouse() {
    return numberOfPenthouse;
  }

  /**
   * @param numberOfPenthouse numberOfPenthouse or {@code null} for none
   */
  public Block setNumberOfPenthouse(Integer numberOfPenthouse) {
    this.numberOfPenthouse = numberOfPenthouse;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getNumberOfUnits() {
    return numberOfUnits;
  }

  /**
   * @param numberOfUnits numberOfUnits or {@code null} for none
   */
  public Block setNumberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getNumberOfUnitsPerFloor() {
    return numberOfUnitsPerFloor;
  }

  /**
   * @param numberOfUnitsPerFloor numberOfUnitsPerFloor or {@code null} for none
   */
  public Block setNumberOfUnitsPerFloor(Integer numberOfUnitsPerFloor) {
    this.numberOfUnitsPerFloor = numberOfUnitsPerFloor;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getOthers() {
    return others;
  }

  /**
   * @param others others or {@code null} for none
   */
  public Block setOthers(String others) {
    this.others = others;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getProjectId() {
    return projectId;
  }

  /**
   * @param projectId projectId or {@code null} for none
   */
  public Block setProjectId(Long projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Long> getPropertyUnits() {
    return propertyUnits;
  }

  /**
   * @param propertyUnits propertyUnits or {@code null} for none
   */
  public Block setPropertyUnits(java.util.List<Long> propertyUnits) {
    this.propertyUnits = propertyUnits;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getTowerStatus() {
    return towerStatus;
  }

  /**
   * @param towerStatus towerStatus or {@code null} for none
   */
  public Block setTowerStatus(String towerStatus) {
    this.towerStatus = towerStatus;
    return this;
  }

  @Override
  public Block set(String fieldName, Object value) {
    return (Block) super.set(fieldName, value);
  }

  @Override
  public Block clone() {
    return (Block) super.clone();
  }

}
