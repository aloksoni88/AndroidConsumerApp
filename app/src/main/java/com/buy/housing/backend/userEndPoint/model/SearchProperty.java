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
 * Model definition for SearchProperty.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SearchProperty extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String address;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer area;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer bath;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer bed;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long bsp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long builderId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String builderName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cityId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean driveView;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean droneView;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String furnished;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String image;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String imageList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String imageResize;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double lat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double lng;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean offer;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer ourPicksScore;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long possession;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long project;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long property;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String sceoURL;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean soldStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer sponsor;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String status;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String type;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean virtualTour;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAddress() {
    return address;
  }

  /**
   * @param address address or {@code null} for none
   */
  public SearchProperty setAddress(java.lang.String address) {
    this.address = address;
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
  public SearchProperty setArea(java.lang.Integer area) {
    this.area = area;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getBath() {
    return bath;
  }

  /**
   * @param bath bath or {@code null} for none
   */
  public SearchProperty setBath(java.lang.Integer bath) {
    this.bath = bath;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getBed() {
    return bed;
  }

  /**
   * @param bed bed or {@code null} for none
   */
  public SearchProperty setBed(java.lang.Integer bed) {
    this.bed = bed;
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
  public SearchProperty setBsp(java.lang.Long bsp) {
    this.bsp = bsp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getBuilderId() {
    return builderId;
  }

  /**
   * @param builderId builderId or {@code null} for none
   */
  public SearchProperty setBuilderId(java.lang.Long builderId) {
    this.builderId = builderId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getBuilderName() {
    return builderName;
  }

  /**
   * @param builderName builderName or {@code null} for none
   */
  public SearchProperty setBuilderName(java.lang.String builderName) {
    this.builderName = builderName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCityId() {
    return cityId;
  }

  /**
   * @param cityId cityId or {@code null} for none
   */
  public SearchProperty setCityId(java.lang.Long cityId) {
    this.cityId = cityId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getDriveView() {
    return driveView;
  }

  /**
   * @param driveView driveView or {@code null} for none
   */
  public SearchProperty setDriveView(java.lang.Boolean driveView) {
    this.driveView = driveView;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getDroneView() {
    return droneView;
  }

  /**
   * @param droneView droneView or {@code null} for none
   */
  public SearchProperty setDroneView(java.lang.Boolean droneView) {
    this.droneView = droneView;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFurnished() {
    return furnished;
  }

  /**
   * @param furnished furnished or {@code null} for none
   */
  public SearchProperty setFurnished(java.lang.String furnished) {
    this.furnished = furnished;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getImage() {
    return image;
  }

  /**
   * @param image image or {@code null} for none
   */
  public SearchProperty setImage(java.lang.String image) {
    this.image = image;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getImageList() {
    return imageList;
  }

  /**
   * @param imageList imageList or {@code null} for none
   */
  public SearchProperty setImageList(java.lang.String imageList) {
    this.imageList = imageList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getImageResize() {
    return imageResize;
  }

  /**
   * @param imageResize imageResize or {@code null} for none
   */
  public SearchProperty setImageResize(java.lang.String imageResize) {
    this.imageResize = imageResize;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getLat() {
    return lat;
  }

  /**
   * @param lat lat or {@code null} for none
   */
  public SearchProperty setLat(java.lang.Double lat) {
    this.lat = lat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getLng() {
    return lng;
  }

  /**
   * @param lng lng or {@code null} for none
   */
  public SearchProperty setLng(java.lang.Double lng) {
    this.lng = lng;
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
  public SearchProperty setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getOffer() {
    return offer;
  }

  /**
   * @param offer offer or {@code null} for none
   */
  public SearchProperty setOffer(java.lang.Boolean offer) {
    this.offer = offer;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getOurPicksScore() {
    return ourPicksScore;
  }

  /**
   * @param ourPicksScore ourPicksScore or {@code null} for none
   */
  public SearchProperty setOurPicksScore(java.lang.Integer ourPicksScore) {
    this.ourPicksScore = ourPicksScore;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getPossession() {
    return possession;
  }

  /**
   * @param possession possession or {@code null} for none
   */
  public SearchProperty setPossession(java.lang.Long possession) {
    this.possession = possession;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getProject() {
    return project;
  }

  /**
   * @param project project or {@code null} for none
   */
  public SearchProperty setProject(java.lang.Long project) {
    this.project = project;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getProperty() {
    return property;
  }

  /**
   * @param property property or {@code null} for none
   */
  public SearchProperty setProperty(java.lang.Long property) {
    this.property = property;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSceoURL() {
    return sceoURL;
  }

  /**
   * @param sceoURL sceoURL or {@code null} for none
   */
  public SearchProperty setSceoURL(java.lang.String sceoURL) {
    this.sceoURL = sceoURL;
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
  public SearchProperty setSoldStatus(java.lang.Boolean soldStatus) {
    this.soldStatus = soldStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getSponsor() {
    return sponsor;
  }

  /**
   * @param sponsor sponsor or {@code null} for none
   */
  public SearchProperty setSponsor(java.lang.Integer sponsor) {
    this.sponsor = sponsor;
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
  public SearchProperty setStatus(java.lang.String status) {
    this.status = status;
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
  public SearchProperty setType(java.lang.String type) {
    this.type = type;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getVirtualTour() {
    return virtualTour;
  }

  /**
   * @param virtualTour virtualTour or {@code null} for none
   */
  public SearchProperty setVirtualTour(java.lang.Boolean virtualTour) {
    this.virtualTour = virtualTour;
    return this;
  }

  @Override
  public SearchProperty set(String fieldName, Object value) {
    return (SearchProperty) super.set(fieldName, value);
  }

  @Override
  public SearchProperty clone() {
    return (SearchProperty) super.clone();
  }

}
