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
 * Model definition for AddOfferRequest.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class AddOfferRequest extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String offer;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long offerEndDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String offerLink;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long offerStartDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long projectId;

  /**
   * @return value or {@code null} for none
   */
  public String getOffer() {
    return offer;
  }

  /**
   * @param offer offer or {@code null} for none
   */
  public AddOfferRequest setOffer(String offer) {
    this.offer = offer;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getOfferEndDate() {
    return offerEndDate;
  }

  /**
   * @param offerEndDate offerEndDate or {@code null} for none
   */
  public AddOfferRequest setOfferEndDate(Long offerEndDate) {
    this.offerEndDate = offerEndDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getOfferLink() {
    return offerLink;
  }

  /**
   * @param offerLink offerLink or {@code null} for none
   */
  public AddOfferRequest setOfferLink(String offerLink) {
    this.offerLink = offerLink;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getOfferStartDate() {
    return offerStartDate;
  }

  /**
   * @param offerStartDate offerStartDate or {@code null} for none
   */
  public AddOfferRequest setOfferStartDate(Long offerStartDate) {
    this.offerStartDate = offerStartDate;
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
  public AddOfferRequest setProjectId(Long projectId) {
    this.projectId = projectId;
    return this;
  }

  @Override
  public AddOfferRequest set(String fieldName, Object value) {
    return (AddOfferRequest) super.set(fieldName, value);
  }

  @Override
  public AddOfferRequest clone() {
    return (AddOfferRequest) super.clone();
  }

}
