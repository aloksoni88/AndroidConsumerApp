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
 * Model definition for LoanListResponse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the loanEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class LoanListResponse extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer accepted;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer count;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer disbursed;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer docCollection;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer enquiry;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer errorId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String errorMessage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer loanByThirdParty;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<LoanLead> loanLeadList;

  static {
    // hack to force ProGuard to consider LoanLead used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(LoanLead.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer loggedIn;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer rejected;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer sanctioned;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer sendToBank;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean status;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getAccepted() {
    return accepted;
  }

  /**
   * @param accepted accepted or {@code null} for none
   */
  public LoanListResponse setAccepted(java.lang.Integer accepted) {
    this.accepted = accepted;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getCount() {
    return count;
  }

  /**
   * @param count count or {@code null} for none
   */
  public LoanListResponse setCount(java.lang.Integer count) {
    this.count = count;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getDisbursed() {
    return disbursed;
  }

  /**
   * @param disbursed disbursed or {@code null} for none
   */
  public LoanListResponse setDisbursed(java.lang.Integer disbursed) {
    this.disbursed = disbursed;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getDocCollection() {
    return docCollection;
  }

  /**
   * @param docCollection docCollection or {@code null} for none
   */
  public LoanListResponse setDocCollection(java.lang.Integer docCollection) {
    this.docCollection = docCollection;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getEnquiry() {
    return enquiry;
  }

  /**
   * @param enquiry enquiry or {@code null} for none
   */
  public LoanListResponse setEnquiry(java.lang.Integer enquiry) {
    this.enquiry = enquiry;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getErrorId() {
    return errorId;
  }

  /**
   * @param errorId errorId or {@code null} for none
   */
  public LoanListResponse setErrorId(java.lang.Integer errorId) {
    this.errorId = errorId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @param errorMessage errorMessage or {@code null} for none
   */
  public LoanListResponse setErrorMessage(java.lang.String errorMessage) {
    this.errorMessage = errorMessage;
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
  public LoanListResponse setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getLoanByThirdParty() {
    return loanByThirdParty;
  }

  /**
   * @param loanByThirdParty loanByThirdParty or {@code null} for none
   */
  public LoanListResponse setLoanByThirdParty(java.lang.Integer loanByThirdParty) {
    this.loanByThirdParty = loanByThirdParty;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<LoanLead> getLoanLeadList() {
    return loanLeadList;
  }

  /**
   * @param loanLeadList loanLeadList or {@code null} for none
   */
  public LoanListResponse setLoanLeadList(java.util.List<LoanLead> loanLeadList) {
    this.loanLeadList = loanLeadList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getLoggedIn() {
    return loggedIn;
  }

  /**
   * @param loggedIn loggedIn or {@code null} for none
   */
  public LoanListResponse setLoggedIn(java.lang.Integer loggedIn) {
    this.loggedIn = loggedIn;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getRejected() {
    return rejected;
  }

  /**
   * @param rejected rejected or {@code null} for none
   */
  public LoanListResponse setRejected(java.lang.Integer rejected) {
    this.rejected = rejected;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getSanctioned() {
    return sanctioned;
  }

  /**
   * @param sanctioned sanctioned or {@code null} for none
   */
  public LoanListResponse setSanctioned(java.lang.Integer sanctioned) {
    this.sanctioned = sanctioned;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getSendToBank() {
    return sendToBank;
  }

  /**
   * @param sendToBank sendToBank or {@code null} for none
   */
  public LoanListResponse setSendToBank(java.lang.Integer sendToBank) {
    this.sendToBank = sendToBank;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getStatus() {
    return status;
  }

  /**
   * @param status status or {@code null} for none
   */
  public LoanListResponse setStatus(java.lang.Boolean status) {
    this.status = status;
    return this;
  }

  @Override
  public LoanListResponse set(String fieldName, Object value) {
    return (LoanListResponse) super.set(fieldName, value);
  }

  @Override
  public LoanListResponse clone() {
    return (LoanListResponse) super.clone();
  }

}
