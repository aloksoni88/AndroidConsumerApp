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
 * on 2019-10-23 at 04:35:07 UTC 
 * Modify at your own risk.
 */

package com.buy.housing.backend.loanEndPoint.model;

/**
 * Model definition for Nominee.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the loanEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Nominee extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String age;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String children;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String city;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String country;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String email;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String fatherName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String gender;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String maritalStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String mobile;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String nationality;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String occupation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String pincode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String relationApplicant;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String residenceAddress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String residencePhone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String state;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String status;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAge() {
    return age;
  }

  /**
   * @param age age or {@code null} for none
   */
  public Nominee setAge(java.lang.String age) {
    this.age = age;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChildren() {
    return children;
  }

  /**
   * @param children children or {@code null} for none
   */
  public Nominee setChildren(java.lang.String children) {
    this.children = children;
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
  public Nominee setCity(java.lang.String city) {
    this.city = city;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCountry() {
    return country;
  }

  /**
   * @param country country or {@code null} for none
   */
  public Nominee setCountry(java.lang.String country) {
    this.country = country;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmail() {
    return email;
  }

  /**
   * @param email email or {@code null} for none
   */
  public Nominee setEmail(java.lang.String email) {
    this.email = email;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFatherName() {
    return fatherName;
  }

  /**
   * @param fatherName fatherName or {@code null} for none
   */
  public Nominee setFatherName(java.lang.String fatherName) {
    this.fatherName = fatherName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getGender() {
    return gender;
  }

  /**
   * @param gender gender or {@code null} for none
   */
  public Nominee setGender(java.lang.String gender) {
    this.gender = gender;
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
  public Nominee setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMaritalStatus() {
    return maritalStatus;
  }

  /**
   * @param maritalStatus maritalStatus or {@code null} for none
   */
  public Nominee setMaritalStatus(java.lang.String maritalStatus) {
    this.maritalStatus = maritalStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMobile() {
    return mobile;
  }

  /**
   * @param mobile mobile or {@code null} for none
   */
  public Nominee setMobile(java.lang.String mobile) {
    this.mobile = mobile;
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
  public Nominee setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNationality() {
    return nationality;
  }

  /**
   * @param nationality nationality or {@code null} for none
   */
  public Nominee setNationality(java.lang.String nationality) {
    this.nationality = nationality;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOccupation() {
    return occupation;
  }

  /**
   * @param occupation occupation or {@code null} for none
   */
  public Nominee setOccupation(java.lang.String occupation) {
    this.occupation = occupation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPincode() {
    return pincode;
  }

  /**
   * @param pincode pincode or {@code null} for none
   */
  public Nominee setPincode(java.lang.String pincode) {
    this.pincode = pincode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getRelationApplicant() {
    return relationApplicant;
  }

  /**
   * @param relationApplicant relationApplicant or {@code null} for none
   */
  public Nominee setRelationApplicant(java.lang.String relationApplicant) {
    this.relationApplicant = relationApplicant;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getResidenceAddress() {
    return residenceAddress;
  }

  /**
   * @param residenceAddress residenceAddress or {@code null} for none
   */
  public Nominee setResidenceAddress(java.lang.String residenceAddress) {
    this.residenceAddress = residenceAddress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getResidencePhone() {
    return residencePhone;
  }

  /**
   * @param residencePhone residencePhone or {@code null} for none
   */
  public Nominee setResidencePhone(java.lang.String residencePhone) {
    this.residencePhone = residencePhone;
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
  public Nominee setState(java.lang.String state) {
    this.state = state;
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
  public Nominee setStatus(java.lang.String status) {
    this.status = status;
    return this;
  }

  @Override
  public Nominee set(String fieldName, Object value) {
    return (Nominee) super.set(fieldName, value);
  }

  @Override
  public Nominee clone() {
    return (Nominee) super.clone();
  }

}
