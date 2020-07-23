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
 * on 2020-02-28 at 09:14:15 UTC 
 * Modify at your own risk.
 */

package com.buy.housing.backend.estimateEndPoint.model;

/**
 * Model definition for EstimateProject.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the estimateEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class EstimateProject extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String address;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String city;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cityId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String exId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

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
  private java.lang.String locality;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float maxArea;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float maxBSP;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float maxBed;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float maxPrice;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float minArea;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float minBSP;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float minBed;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float minPrice;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long possession;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String propertyStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String searchNameInUP;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long time;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String type;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAddress() {
    return address;
  }

  /**
   * @param address address or {@code null} for none
   */
  public EstimateProject setAddress(java.lang.String address) {
    this.address = address;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCity() {
    return city;
  }

  /**
   * @param city city or {@code null} for none
   */
  public EstimateProject setCity(java.lang.String city) {
    this.city = city;
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
  public EstimateProject setCityId(java.lang.Long cityId) {
    this.cityId = cityId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getExId() {
    return exId;
  }

  /**
   * @param exId exId or {@code null} for none
   */
  public EstimateProject setExId(java.lang.String exId) {
    this.exId = exId;
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
  public EstimateProject setId(java.lang.Long id) {
    this.id = id;
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
  public EstimateProject setLat(java.lang.Double lat) {
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
  public EstimateProject setLng(java.lang.Double lng) {
    this.lng = lng;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLocality() {
    return locality;
  }

  /**
   * @param locality locality or {@code null} for none
   */
  public EstimateProject setLocality(java.lang.String locality) {
    this.locality = locality;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMaxArea() {
    return maxArea;
  }

  /**
   * @param maxArea maxArea or {@code null} for none
   */
  public EstimateProject setMaxArea(java.lang.Float maxArea) {
    this.maxArea = maxArea;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMaxBSP() {
    return maxBSP;
  }

  /**
   * @param maxBSP maxBSP or {@code null} for none
   */
  public EstimateProject setMaxBSP(java.lang.Float maxBSP) {
    this.maxBSP = maxBSP;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMaxBed() {
    return maxBed;
  }

  /**
   * @param maxBed maxBed or {@code null} for none
   */
  public EstimateProject setMaxBed(java.lang.Float maxBed) {
    this.maxBed = maxBed;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMaxPrice() {
    return maxPrice;
  }

  /**
   * @param maxPrice maxPrice or {@code null} for none
   */
  public EstimateProject setMaxPrice(java.lang.Float maxPrice) {
    this.maxPrice = maxPrice;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMinArea() {
    return minArea;
  }

  /**
   * @param minArea minArea or {@code null} for none
   */
  public EstimateProject setMinArea(java.lang.Float minArea) {
    this.minArea = minArea;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMinBSP() {
    return minBSP;
  }

  /**
   * @param minBSP minBSP or {@code null} for none
   */
  public EstimateProject setMinBSP(java.lang.Float minBSP) {
    this.minBSP = minBSP;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMinBed() {
    return minBed;
  }

  /**
   * @param minBed minBed or {@code null} for none
   */
  public EstimateProject setMinBed(java.lang.Float minBed) {
    this.minBed = minBed;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getMinPrice() {
    return minPrice;
  }

  /**
   * @param minPrice minPrice or {@code null} for none
   */
  public EstimateProject setMinPrice(java.lang.Float minPrice) {
    this.minPrice = minPrice;
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
  public EstimateProject setName(java.lang.String name) {
    this.name = name;
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
  public EstimateProject setPossession(java.lang.Long possession) {
    this.possession = possession;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPropertyStatus() {
    return propertyStatus;
  }

  /**
   * @param propertyStatus propertyStatus or {@code null} for none
   */
  public EstimateProject setPropertyStatus(java.lang.String propertyStatus) {
    this.propertyStatus = propertyStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSearchNameInUP() {
    return searchNameInUP;
  }

  /**
   * @param searchNameInUP searchNameInUP or {@code null} for none
   */
  public EstimateProject setSearchNameInUP(java.lang.String searchNameInUP) {
    this.searchNameInUP = searchNameInUP;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTime() {
    return time;
  }

  /**
   * @param time time or {@code null} for none
   */
  public EstimateProject setTime(java.lang.Long time) {
    this.time = time;
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
  public EstimateProject setType(java.lang.String type) {
    this.type = type;
    return this;
  }

  @Override
  public EstimateProject set(String fieldName, Object value) {
    return (EstimateProject) super.set(fieldName, value);
  }

  @Override
  public EstimateProject clone() {
    return (EstimateProject) super.clone();
  }

}
