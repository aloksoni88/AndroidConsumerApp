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
 * Model definition for PropertyUnit.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class PropertyUnit extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long blockId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long bookingId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String facing;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer floorNumber;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> holdIdList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String houseNumber;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String investor;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long layoutId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean onHold;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> plcFactors;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long projectId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long propertyTypeId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer wingNumber;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getBlockId() {
    return blockId;
  }

  /**
   * @param blockId blockId or {@code null} for none
   */
  public PropertyUnit setBlockId(java.lang.Long blockId) {
    this.blockId = blockId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getBookingId() {
    return bookingId;
  }

  /**
   * @param bookingId bookingId or {@code null} for none
   */
  public PropertyUnit setBookingId(java.lang.Long bookingId) {
    this.bookingId = bookingId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFacing() {
    return facing;
  }

  /**
   * @param facing facing or {@code null} for none
   */
  public PropertyUnit setFacing(java.lang.String facing) {
    this.facing = facing;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getFloorNumber() {
    return floorNumber;
  }

  /**
   * @param floorNumber floorNumber or {@code null} for none
   */
  public PropertyUnit setFloorNumber(java.lang.Integer floorNumber) {
    this.floorNumber = floorNumber;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getHoldIdList() {
    return holdIdList;
  }

  /**
   * @param holdIdList holdIdList or {@code null} for none
   */
  public PropertyUnit setHoldIdList(java.util.List<java.lang.Long> holdIdList) {
    this.holdIdList = holdIdList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getHouseNumber() {
    return houseNumber;
  }

  /**
   * @param houseNumber houseNumber or {@code null} for none
   */
  public PropertyUnit setHouseNumber(java.lang.String houseNumber) {
    this.houseNumber = houseNumber;
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
  public PropertyUnit setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getInvestor() {
    return investor;
  }

  /**
   * @param investor investor or {@code null} for none
   */
  public PropertyUnit setInvestor(java.lang.String investor) {
    this.investor = investor;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getLayoutId() {
    return layoutId;
  }

  /**
   * @param layoutId layoutId or {@code null} for none
   */
  public PropertyUnit setLayoutId(java.lang.Long layoutId) {
    this.layoutId = layoutId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getOnHold() {
    return onHold;
  }

  /**
   * @param onHold onHold or {@code null} for none
   */
  public PropertyUnit setOnHold(java.lang.Boolean onHold) {
    this.onHold = onHold;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getPlcFactors() {
    return plcFactors;
  }

  /**
   * @param plcFactors plcFactors or {@code null} for none
   */
  public PropertyUnit setPlcFactors(java.util.List<java.lang.String> plcFactors) {
    this.plcFactors = plcFactors;
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
  public PropertyUnit setProjectId(java.lang.Long projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getPropertyTypeId() {
    return propertyTypeId;
  }

  /**
   * @param propertyTypeId propertyTypeId or {@code null} for none
   */
  public PropertyUnit setPropertyTypeId(java.lang.Long propertyTypeId) {
    this.propertyTypeId = propertyTypeId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getWingNumber() {
    return wingNumber;
  }

  /**
   * @param wingNumber wingNumber or {@code null} for none
   */
  public PropertyUnit setWingNumber(java.lang.Integer wingNumber) {
    this.wingNumber = wingNumber;
    return this;
  }

  @Override
  public PropertyUnit set(String fieldName, Object value) {
    return (PropertyUnit) super.set(fieldName, value);
  }

  @Override
  public PropertyUnit clone() {
    return (PropertyUnit) super.clone();
  }

}
