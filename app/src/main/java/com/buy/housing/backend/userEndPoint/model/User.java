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
 * Model definition for User.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class User extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String address;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String addressProofType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String addressProofUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String adhaarNo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String adhaarUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String appVersion;

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
  private java.lang.String company;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String countryName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String countrySTDCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String designation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> directors;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long dob;

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
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> favouriteProjectLIst;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String fax;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String fcmkey;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String form60;

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
  private java.lang.String imageURL;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String kycCompany;

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
  private java.lang.String loginType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String mPIN;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String maritalStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String memorandumOfArticle;

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
  private java.lang.String nriDeclarationForm;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String officeAddress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String officePhone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String ostype;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String otherEmail;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String otherPhone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String panNo;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String panUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String passport;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String password;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String permanentAddress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String permanentCity;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String permanentPin;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String permanentState;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String phone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String profession;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String residencePhone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String residentType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String residentialStatus;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<SearchCriteria> searchCriteriaList;

  static {
    // hack to force ProGuard to consider SearchCriteria used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(SearchCriteria.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String spouseName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String state;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String thirdPartyID;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long time;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String userType;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAddress() {
    return address;
  }

  /**
   * @param address address or {@code null} for none
   */
  public User setAddress(java.lang.String address) {
    this.address = address;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAddressProofType() {
    return addressProofType;
  }

  /**
   * @param addressProofType addressProofType or {@code null} for none
   */
  public User setAddressProofType(java.lang.String addressProofType) {
    this.addressProofType = addressProofType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAddressProofUrl() {
    return addressProofUrl;
  }

  /**
   * @param addressProofUrl addressProofUrl or {@code null} for none
   */
  public User setAddressProofUrl(java.lang.String addressProofUrl) {
    this.addressProofUrl = addressProofUrl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAdhaarNo() {
    return adhaarNo;
  }

  /**
   * @param adhaarNo adhaarNo or {@code null} for none
   */
  public User setAdhaarNo(java.lang.String adhaarNo) {
    this.adhaarNo = adhaarNo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAdhaarUrl() {
    return adhaarUrl;
  }

  /**
   * @param adhaarUrl adhaarUrl or {@code null} for none
   */
  public User setAdhaarUrl(java.lang.String adhaarUrl) {
    this.adhaarUrl = adhaarUrl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAppVersion() {
    return appVersion;
  }

  /**
   * @param appVersion appVersion or {@code null} for none
   */
  public User setAppVersion(java.lang.String appVersion) {
    this.appVersion = appVersion;
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
  public User setChildren(java.lang.String children) {
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
  public User setCity(java.lang.String city) {
    this.city = city;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCompany() {
    return company;
  }

  /**
   * @param company company or {@code null} for none
   */
  public User setCompany(java.lang.String company) {
    this.company = company;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCountryName() {
    return countryName;
  }

  /**
   * @param countryName countryName or {@code null} for none
   */
  public User setCountryName(java.lang.String countryName) {
    this.countryName = countryName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCountrySTDCode() {
    return countrySTDCode;
  }

  /**
   * @param countrySTDCode countrySTDCode or {@code null} for none
   */
  public User setCountrySTDCode(java.lang.String countrySTDCode) {
    this.countrySTDCode = countrySTDCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDesignation() {
    return designation;
  }

  /**
   * @param designation designation or {@code null} for none
   */
  public User setDesignation(java.lang.String designation) {
    this.designation = designation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getDirectors() {
    return directors;
  }

  /**
   * @param directors directors or {@code null} for none
   */
  public User setDirectors(java.util.List<java.lang.String> directors) {
    this.directors = directors;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getDob() {
    return dob;
  }

  /**
   * @param dob dob or {@code null} for none
   */
  public User setDob(java.lang.Long dob) {
    this.dob = dob;
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
  public User setEmail(java.lang.String email) {
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
  public User setFatherName(java.lang.String fatherName) {
    this.fatherName = fatherName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getFavouriteProjectLIst() {
    return favouriteProjectLIst;
  }

  /**
   * @param favouriteProjectLIst favouriteProjectLIst or {@code null} for none
   */
  public User setFavouriteProjectLIst(java.util.List<java.lang.Long> favouriteProjectLIst) {
    this.favouriteProjectLIst = favouriteProjectLIst;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFax() {
    return fax;
  }

  /**
   * @param fax fax or {@code null} for none
   */
  public User setFax(java.lang.String fax) {
    this.fax = fax;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFcmkey() {
    return fcmkey;
  }

  /**
   * @param fcmkey fcmkey or {@code null} for none
   */
  public User setFcmkey(java.lang.String fcmkey) {
    this.fcmkey = fcmkey;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getForm60() {
    return form60;
  }

  /**
   * @param form60 form60 or {@code null} for none
   */
  public User setForm60(java.lang.String form60) {
    this.form60 = form60;
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
  public User setGender(java.lang.String gender) {
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
  public User setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getImageURL() {
    return imageURL;
  }

  /**
   * @param imageURL imageURL or {@code null} for none
   */
  public User setImageURL(java.lang.String imageURL) {
    this.imageURL = imageURL;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getKycCompany() {
    return kycCompany;
  }

  /**
   * @param kycCompany kycCompany or {@code null} for none
   */
  public User setKycCompany(java.lang.String kycCompany) {
    this.kycCompany = kycCompany;
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
  public User setLat(java.lang.Double lat) {
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
  public User setLng(java.lang.Double lng) {
    this.lng = lng;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLoginType() {
    return loginType;
  }

  /**
   * @param loginType loginType or {@code null} for none
   */
  public User setLoginType(java.lang.String loginType) {
    this.loginType = loginType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMPIN() {
    return mPIN;
  }

  /**
   * @param mPIN mPIN or {@code null} for none
   */
  public User setMPIN(java.lang.String mPIN) {
    this.mPIN = mPIN;
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
  public User setMaritalStatus(java.lang.String maritalStatus) {
    this.maritalStatus = maritalStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMemorandumOfArticle() {
    return memorandumOfArticle;
  }

  /**
   * @param memorandumOfArticle memorandumOfArticle or {@code null} for none
   */
  public User setMemorandumOfArticle(java.lang.String memorandumOfArticle) {
    this.memorandumOfArticle = memorandumOfArticle;
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
  public User setName(java.lang.String name) {
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
  public User setNationality(java.lang.String nationality) {
    this.nationality = nationality;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNriDeclarationForm() {
    return nriDeclarationForm;
  }

  /**
   * @param nriDeclarationForm nriDeclarationForm or {@code null} for none
   */
  public User setNriDeclarationForm(java.lang.String nriDeclarationForm) {
    this.nriDeclarationForm = nriDeclarationForm;
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
  public User setOfficeAddress(java.lang.String officeAddress) {
    this.officeAddress = officeAddress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOfficePhone() {
    return officePhone;
  }

  /**
   * @param officePhone officePhone or {@code null} for none
   */
  public User setOfficePhone(java.lang.String officePhone) {
    this.officePhone = officePhone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOstype() {
    return ostype;
  }

  /**
   * @param ostype ostype or {@code null} for none
   */
  public User setOstype(java.lang.String ostype) {
    this.ostype = ostype;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOtherEmail() {
    return otherEmail;
  }

  /**
   * @param otherEmail otherEmail or {@code null} for none
   */
  public User setOtherEmail(java.lang.String otherEmail) {
    this.otherEmail = otherEmail;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOtherPhone() {
    return otherPhone;
  }

  /**
   * @param otherPhone otherPhone or {@code null} for none
   */
  public User setOtherPhone(java.lang.String otherPhone) {
    this.otherPhone = otherPhone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPanNo() {
    return panNo;
  }

  /**
   * @param panNo panNo or {@code null} for none
   */
  public User setPanNo(java.lang.String panNo) {
    this.panNo = panNo;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPanUrl() {
    return panUrl;
  }

  /**
   * @param panUrl panUrl or {@code null} for none
   */
  public User setPanUrl(java.lang.String panUrl) {
    this.panUrl = panUrl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPassport() {
    return passport;
  }

  /**
   * @param passport passport or {@code null} for none
   */
  public User setPassport(java.lang.String passport) {
    this.passport = passport;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPassword() {
    return password;
  }

  /**
   * @param password password or {@code null} for none
   */
  public User setPassword(java.lang.String password) {
    this.password = password;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPermanentAddress() {
    return permanentAddress;
  }

  /**
   * @param permanentAddress permanentAddress or {@code null} for none
   */
  public User setPermanentAddress(java.lang.String permanentAddress) {
    this.permanentAddress = permanentAddress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPermanentCity() {
    return permanentCity;
  }

  /**
   * @param permanentCity permanentCity or {@code null} for none
   */
  public User setPermanentCity(java.lang.String permanentCity) {
    this.permanentCity = permanentCity;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPermanentPin() {
    return permanentPin;
  }

  /**
   * @param permanentPin permanentPin or {@code null} for none
   */
  public User setPermanentPin(java.lang.String permanentPin) {
    this.permanentPin = permanentPin;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPermanentState() {
    return permanentState;
  }

  /**
   * @param permanentState permanentState or {@code null} for none
   */
  public User setPermanentState(java.lang.String permanentState) {
    this.permanentState = permanentState;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhone() {
    return phone;
  }

  /**
   * @param phone phone or {@code null} for none
   */
  public User setPhone(java.lang.String phone) {
    this.phone = phone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getProfession() {
    return profession;
  }

  /**
   * @param profession profession or {@code null} for none
   */
  public User setProfession(java.lang.String profession) {
    this.profession = profession;
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
  public User setResidencePhone(java.lang.String residencePhone) {
    this.residencePhone = residencePhone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getResidentType() {
    return residentType;
  }

  /**
   * @param residentType residentType or {@code null} for none
   */
  public User setResidentType(java.lang.String residentType) {
    this.residentType = residentType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getResidentialStatus() {
    return residentialStatus;
  }

  /**
   * @param residentialStatus residentialStatus or {@code null} for none
   */
  public User setResidentialStatus(java.lang.String residentialStatus) {
    this.residentialStatus = residentialStatus;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<SearchCriteria> getSearchCriteriaList() {
    return searchCriteriaList;
  }

  /**
   * @param searchCriteriaList searchCriteriaList or {@code null} for none
   */
  public User setSearchCriteriaList(java.util.List<SearchCriteria> searchCriteriaList) {
    this.searchCriteriaList = searchCriteriaList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSpouseName() {
    return spouseName;
  }

  /**
   * @param spouseName spouseName or {@code null} for none
   */
  public User setSpouseName(java.lang.String spouseName) {
    this.spouseName = spouseName;
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
  public User setState(java.lang.String state) {
    this.state = state;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getThirdPartyID() {
    return thirdPartyID;
  }

  /**
   * @param thirdPartyID thirdPartyID or {@code null} for none
   */
  public User setThirdPartyID(java.lang.String thirdPartyID) {
    this.thirdPartyID = thirdPartyID;
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
  public User setTime(java.lang.Long time) {
    this.time = time;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUserType() {
    return userType;
  }

  /**
   * @param userType userType or {@code null} for none
   */
  public User setUserType(java.lang.String userType) {
    this.userType = userType;
    return this;
  }

  @Override
  public User set(String fieldName, Object value) {
    return (User) super.set(fieldName, value);
  }

  @Override
  public User clone() {
    return (User) super.clone();
  }

}