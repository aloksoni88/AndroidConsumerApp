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
 * Model definition for Builder.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Builder extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Contact> contactList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Document> documents;

  static {
    // hack to force ProGuard to consider Document used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Document.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long establishDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean hide;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String logoURL;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String officeAddress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String officeCity;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String officeCountry;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String officePin;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String officeState;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer projectDelayed;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer projectDelivered;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer projectInProgress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer projectStarted;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String slogoURL;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String summary;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String webURL;

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Contact> getContactList() {
    return contactList;
  }

  /**
   * @param contactList contactList or {@code null} for none
   */
  public Builder setContactList(java.util.List<Contact> contactList) {
    this.contactList = contactList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Document> getDocuments() {
    return documents;
  }

  /**
   * @param documents documents or {@code null} for none
   */
  public Builder setDocuments(java.util.List<Document> documents) {
    this.documents = documents;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getEstablishDate() {
    return establishDate;
  }

  /**
   * @param establishDate establishDate or {@code null} for none
   */
  public Builder setEstablishDate(java.lang.Long establishDate) {
    this.establishDate = establishDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getHide() {
    return hide;
  }

  /**
   * @param hide hide or {@code null} for none
   */
  public Builder setHide(java.lang.Boolean hide) {
    this.hide = hide;
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
  public Builder setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLogoURL() {
    return logoURL;
  }

  /**
   * @param logoURL logoURL or {@code null} for none
   */
  public Builder setLogoURL(java.lang.String logoURL) {
    this.logoURL = logoURL;
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
  public Builder setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOfficeAddress() {
    return officeAddress;
  }

  /**
   * @param officeAddress officeAddress or {@code null} for none
   */
  public Builder setOfficeAddress(java.lang.String officeAddress) {
    this.officeAddress = officeAddress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOfficeCity() {
    return officeCity;
  }

  /**
   * @param officeCity officeCity or {@code null} for none
   */
  public Builder setOfficeCity(java.lang.String officeCity) {
    this.officeCity = officeCity;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOfficeCountry() {
    return officeCountry;
  }

  /**
   * @param officeCountry officeCountry or {@code null} for none
   */
  public Builder setOfficeCountry(java.lang.String officeCountry) {
    this.officeCountry = officeCountry;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOfficePin() {
    return officePin;
  }

  /**
   * @param officePin officePin or {@code null} for none
   */
  public Builder setOfficePin(java.lang.String officePin) {
    this.officePin = officePin;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOfficeState() {
    return officeState;
  }

  /**
   * @param officeState officeState or {@code null} for none
   */
  public Builder setOfficeState(java.lang.String officeState) {
    this.officeState = officeState;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getProjectDelayed() {
    return projectDelayed;
  }

  /**
   * @param projectDelayed projectDelayed or {@code null} for none
   */
  public Builder setProjectDelayed(java.lang.Integer projectDelayed) {
    this.projectDelayed = projectDelayed;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getProjectDelivered() {
    return projectDelivered;
  }

  /**
   * @param projectDelivered projectDelivered or {@code null} for none
   */
  public Builder setProjectDelivered(java.lang.Integer projectDelivered) {
    this.projectDelivered = projectDelivered;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getProjectInProgress() {
    return projectInProgress;
  }

  /**
   * @param projectInProgress projectInProgress or {@code null} for none
   */
  public Builder setProjectInProgress(java.lang.Integer projectInProgress) {
    this.projectInProgress = projectInProgress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getProjectStarted() {
    return projectStarted;
  }

  /**
   * @param projectStarted projectStarted or {@code null} for none
   */
  public Builder setProjectStarted(java.lang.Integer projectStarted) {
    this.projectStarted = projectStarted;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSlogoURL() {
    return slogoURL;
  }

  /**
   * @param slogoURL slogoURL or {@code null} for none
   */
  public Builder setSlogoURL(java.lang.String slogoURL) {
    this.slogoURL = slogoURL;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSummary() {
    return summary;
  }

  /**
   * @param summary summary or {@code null} for none
   */
  public Builder setSummary(java.lang.String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getWebURL() {
    return webURL;
  }

  /**
   * @param webURL webURL or {@code null} for none
   */
  public Builder setWebURL(java.lang.String webURL) {
    this.webURL = webURL;
    return this;
  }

  @Override
  public Builder set(String fieldName, Object value) {
    return (Builder) super.set(fieldName, value);
  }

  @Override
  public Builder clone() {
    return (Builder) super.clone();
  }

}
