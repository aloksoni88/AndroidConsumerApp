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
 * Model definition for CommisionSlab.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class CommisionSlab extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Float additional;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Float collection;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Float commision;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String commisionType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer endRange;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer startRange;

  /**
   * @return value or {@code null} for none
   */
  public Float getAdditional() {
    return additional;
  }

  /**
   * @param additional additional or {@code null} for none
   */
  public CommisionSlab setAdditional(Float additional) {
    this.additional = additional;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Float getCollection() {
    return collection;
  }

  /**
   * @param collection collection or {@code null} for none
   */
  public CommisionSlab setCollection(Float collection) {
    this.collection = collection;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Float getCommision() {
    return commision;
  }

  /**
   * @param commision commision or {@code null} for none
   */
  public CommisionSlab setCommision(Float commision) {
    this.commision = commision;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getCommisionType() {
    return commisionType;
  }

  /**
   * @param commisionType commisionType or {@code null} for none
   */
  public CommisionSlab setCommisionType(String commisionType) {
    this.commisionType = commisionType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getEndRange() {
    return endRange;
  }

  /**
   * @param endRange endRange or {@code null} for none
   */
  public CommisionSlab setEndRange(Integer endRange) {
    this.endRange = endRange;
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
  public CommisionSlab setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getStartRange() {
    return startRange;
  }

  /**
   * @param startRange startRange or {@code null} for none
   */
  public CommisionSlab setStartRange(Integer startRange) {
    this.startRange = startRange;
    return this;
  }

  @Override
  public CommisionSlab set(String fieldName, Object value) {
    return (CommisionSlab) super.set(fieldName, value);
  }

  @Override
  public CommisionSlab clone() {
    return (CommisionSlab) super.clone();
  }

}
