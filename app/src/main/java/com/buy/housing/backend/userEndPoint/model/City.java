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
 * Model definition for City.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class City extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String country;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean estimate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String gstNumber;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String kmlURL;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double lat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean live;

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
  private java.lang.String pName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer projectCount;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String reraId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String searchName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String state;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long stateId;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCountry() {
    return country;
  }

  /**
   * @param country country or {@code null} for none
   */
  public City setCountry(java.lang.String country) {
    this.country = country;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getEstimate() {
    return estimate;
  }

  /**
   * @param estimate estimate or {@code null} for none
   */
  public City setEstimate(java.lang.Boolean estimate) {
    this.estimate = estimate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getGstNumber() {
    return gstNumber;
  }

  /**
   * @param gstNumber gstNumber or {@code null} for none
   */
  public City setGstNumber(java.lang.String gstNumber) {
    this.gstNumber = gstNumber;
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
  public City setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getKmlURL() {
    return kmlURL;
  }

  /**
   * @param kmlURL kmlURL or {@code null} for none
   */
  public City setKmlURL(java.lang.String kmlURL) {
    this.kmlURL = kmlURL;
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
  public City setLat(java.lang.Double lat) {
    this.lat = lat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getLive() {
    return live;
  }

  /**
   * @param live live or {@code null} for none
   */
  public City setLive(java.lang.Boolean live) {
    this.live = live;
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
  public City setLng(java.lang.Double lng) {
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
  public City setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPName() {
    return pName;
  }

  /**
   * @param pName pName or {@code null} for none
   */
  public City setPName(java.lang.String pName) {
    this.pName = pName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getProjectCount() {
    return projectCount;
  }

  /**
   * @param projectCount projectCount or {@code null} for none
   */
  public City setProjectCount(java.lang.Integer projectCount) {
    this.projectCount = projectCount;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getReraId() {
    return reraId;
  }

  /**
   * @param reraId reraId or {@code null} for none
   */
  public City setReraId(java.lang.String reraId) {
    this.reraId = reraId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSearchName() {
    return searchName;
  }

  /**
   * @param searchName searchName or {@code null} for none
   */
  public City setSearchName(java.lang.String searchName) {
    this.searchName = searchName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getState() {
    return state;
  }

  /**
   * @param state state or {@code null} for none
   */
  public City setState(java.lang.String state) {
    this.state = state;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getStateId() {
    return stateId;
  }

  /**
   * @param stateId stateId or {@code null} for none
   */
  public City setStateId(java.lang.Long stateId) {
    this.stateId = stateId;
    return this;
  }

  @Override
  public City set(String fieldName, Object value) {
    return (City) super.set(fieldName, value);
  }

  @Override
  public City clone() {
    return (City) super.clone();
  }

}
