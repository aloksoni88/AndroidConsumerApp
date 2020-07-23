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
 * Model definition for PlaceInfo.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class PlaceInfo extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String address;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer area;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer bath;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer bed;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long bsp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long builderId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String builderName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long cityId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Double distanceInKilometers;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean driveView;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean droneView;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String furnished;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String image;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String imageList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String imageResize;

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
  private Boolean offer;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer ourPicksScore;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long possession;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long project;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long property;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String sceoURL;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean soldStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer sponsor;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String status;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String type;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean virtualTour;

  /**
   * @return value or {@code null} for none
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address address or {@code null} for none
   */
  public PlaceInfo setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getArea() {
    return area;
  }

  /**
   * @param area area or {@code null} for none
   */
  public PlaceInfo setArea(Integer area) {
    this.area = area;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getBath() {
    return bath;
  }

  /**
   * @param bath bath or {@code null} for none
   */
  public PlaceInfo setBath(Integer bath) {
    this.bath = bath;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getBed() {
    return bed;
  }

  /**
   * @param bed bed or {@code null} for none
   */
  public PlaceInfo setBed(Integer bed) {
    this.bed = bed;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getBsp() {
    return bsp;
  }

  /**
   * @param bsp bsp or {@code null} for none
   */
  public PlaceInfo setBsp(Long bsp) {
    this.bsp = bsp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getBuilderId() {
    return builderId;
  }

  /**
   * @param builderId builderId or {@code null} for none
   */
  public PlaceInfo setBuilderId(Long builderId) {
    this.builderId = builderId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getBuilderName() {
    return builderName;
  }

  /**
   * @param builderName builderName or {@code null} for none
   */
  public PlaceInfo setBuilderName(String builderName) {
    this.builderName = builderName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getCityId() {
    return cityId;
  }

  /**
   * @param cityId cityId or {@code null} for none
   */
  public PlaceInfo setCityId(Long cityId) {
    this.cityId = cityId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Double getDistanceInKilometers() {
    return distanceInKilometers;
  }

  /**
   * @param distanceInKilometers distanceInKilometers or {@code null} for none
   */
  public PlaceInfo setDistanceInKilometers(Double distanceInKilometers) {
    this.distanceInKilometers = distanceInKilometers;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getDriveView() {
    return driveView;
  }

  /**
   * @param driveView driveView or {@code null} for none
   */
  public PlaceInfo setDriveView(Boolean driveView) {
    this.driveView = driveView;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getDroneView() {
    return droneView;
  }

  /**
   * @param droneView droneView or {@code null} for none
   */
  public PlaceInfo setDroneView(Boolean droneView) {
    this.droneView = droneView;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getFurnished() {
    return furnished;
  }

  /**
   * @param furnished furnished or {@code null} for none
   */
  public PlaceInfo setFurnished(String furnished) {
    this.furnished = furnished;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getImage() {
    return image;
  }

  /**
   * @param image image or {@code null} for none
   */
  public PlaceInfo setImage(String image) {
    this.image = image;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getImageList() {
    return imageList;
  }

  /**
   * @param imageList imageList or {@code null} for none
   */
  public PlaceInfo setImageList(String imageList) {
    this.imageList = imageList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getImageResize() {
    return imageResize;
  }

  /**
   * @param imageResize imageResize or {@code null} for none
   */
  public PlaceInfo setImageResize(String imageResize) {
    this.imageResize = imageResize;
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
  public PlaceInfo setLat(Double lat) {
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
  public PlaceInfo setLng(Double lng) {
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
  public PlaceInfo setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getOffer() {
    return offer;
  }

  /**
   * @param offer offer or {@code null} for none
   */
  public PlaceInfo setOffer(Boolean offer) {
    this.offer = offer;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getOurPicksScore() {
    return ourPicksScore;
  }

  /**
   * @param ourPicksScore ourPicksScore or {@code null} for none
   */
  public PlaceInfo setOurPicksScore(Integer ourPicksScore) {
    this.ourPicksScore = ourPicksScore;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getPossession() {
    return possession;
  }

  /**
   * @param possession possession or {@code null} for none
   */
  public PlaceInfo setPossession(Long possession) {
    this.possession = possession;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getProject() {
    return project;
  }

  /**
   * @param project project or {@code null} for none
   */
  public PlaceInfo setProject(Long project) {
    this.project = project;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getProperty() {
    return property;
  }

  /**
   * @param property property or {@code null} for none
   */
  public PlaceInfo setProperty(Long property) {
    this.property = property;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getSceoURL() {
    return sceoURL;
  }

  /**
   * @param sceoURL sceoURL or {@code null} for none
   */
  public PlaceInfo setSceoURL(String sceoURL) {
    this.sceoURL = sceoURL;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getSoldStatus() {
    return soldStatus;
  }

  /**
   * @param soldStatus soldStatus or {@code null} for none
   */
  public PlaceInfo setSoldStatus(Boolean soldStatus) {
    this.soldStatus = soldStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getSponsor() {
    return sponsor;
  }

  /**
   * @param sponsor sponsor or {@code null} for none
   */
  public PlaceInfo setSponsor(Integer sponsor) {
    this.sponsor = sponsor;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status status or {@code null} for none
   */
  public PlaceInfo setStatus(String status) {
    this.status = status;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getType() {
    return type;
  }

  /**
   * @param type type or {@code null} for none
   */
  public PlaceInfo setType(String type) {
    this.type = type;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getVirtualTour() {
    return virtualTour;
  }

  /**
   * @param virtualTour virtualTour or {@code null} for none
   */
  public PlaceInfo setVirtualTour(Boolean virtualTour) {
    this.virtualTour = virtualTour;
    return this;
  }

  @Override
  public PlaceInfo set(String fieldName, Object value) {
    return (PlaceInfo) super.set(fieldName, value);
  }

  @Override
  public PlaceInfo clone() {
    return (PlaceInfo) super.clone();
  }

}
