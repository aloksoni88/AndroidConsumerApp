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
 * Model definition for GetProjectSortDetailResponse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class GetProjectSortDetailResponse extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String address;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String bspRange;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String builderName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String city;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String coverImage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer errorId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String errorMessage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String metaDescription;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String metaKey;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String metaTitle;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String priceRange;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String projectSummary;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String propertyTypeRange;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String sceoURL;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String sizeRange;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean status;

  /**
   * @return value or {@code null} for none
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address address or {@code null} for none
   */
  public GetProjectSortDetailResponse setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getBspRange() {
    return bspRange;
  }

  /**
   * @param bspRange bspRange or {@code null} for none
   */
  public GetProjectSortDetailResponse setBspRange(String bspRange) {
    this.bspRange = bspRange;
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
  public GetProjectSortDetailResponse setBuilderName(String builderName) {
    this.builderName = builderName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city city or {@code null} for none
   */
  public GetProjectSortDetailResponse setCity(String city) {
    this.city = city;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getCoverImage() {
    return coverImage;
  }

  /**
   * @param coverImage coverImage or {@code null} for none
   */
  public GetProjectSortDetailResponse setCoverImage(String coverImage) {
    this.coverImage = coverImage;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getErrorId() {
    return errorId;
  }

  /**
   * @param errorId errorId or {@code null} for none
   */
  public GetProjectSortDetailResponse setErrorId(Integer errorId) {
    this.errorId = errorId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @param errorMessage errorMessage or {@code null} for none
   */
  public GetProjectSortDetailResponse setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
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
  public GetProjectSortDetailResponse setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getMetaDescription() {
    return metaDescription;
  }

  /**
   * @param metaDescription metaDescription or {@code null} for none
   */
  public GetProjectSortDetailResponse setMetaDescription(String metaDescription) {
    this.metaDescription = metaDescription;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getMetaKey() {
    return metaKey;
  }

  /**
   * @param metaKey metaKey or {@code null} for none
   */
  public GetProjectSortDetailResponse setMetaKey(String metaKey) {
    this.metaKey = metaKey;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getMetaTitle() {
    return metaTitle;
  }

  /**
   * @param metaTitle metaTitle or {@code null} for none
   */
  public GetProjectSortDetailResponse setMetaTitle(String metaTitle) {
    this.metaTitle = metaTitle;
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
  public GetProjectSortDetailResponse setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getPriceRange() {
    return priceRange;
  }

  /**
   * @param priceRange priceRange or {@code null} for none
   */
  public GetProjectSortDetailResponse setPriceRange(String priceRange) {
    this.priceRange = priceRange;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getProjectSummary() {
    return projectSummary;
  }

  /**
   * @param projectSummary projectSummary or {@code null} for none
   */
  public GetProjectSortDetailResponse setProjectSummary(String projectSummary) {
    this.projectSummary = projectSummary;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getPropertyTypeRange() {
    return propertyTypeRange;
  }

  /**
   * @param propertyTypeRange propertyTypeRange or {@code null} for none
   */
  public GetProjectSortDetailResponse setPropertyTypeRange(String propertyTypeRange) {
    this.propertyTypeRange = propertyTypeRange;
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
  public GetProjectSortDetailResponse setSceoURL(String sceoURL) {
    this.sceoURL = sceoURL;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getSizeRange() {
    return sizeRange;
  }

  /**
   * @param sizeRange sizeRange or {@code null} for none
   */
  public GetProjectSortDetailResponse setSizeRange(String sizeRange) {
    this.sizeRange = sizeRange;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getStatus() {
    return status;
  }

  /**
   * @param status status or {@code null} for none
   */
  public GetProjectSortDetailResponse setStatus(Boolean status) {
    this.status = status;
    return this;
  }

  @Override
  public GetProjectSortDetailResponse set(String fieldName, Object value) {
    return (GetProjectSortDetailResponse) super.set(fieldName, value);
  }

  @Override
  public GetProjectSortDetailResponse clone() {
    return (GetProjectSortDetailResponse) super.clone();
  }

}
