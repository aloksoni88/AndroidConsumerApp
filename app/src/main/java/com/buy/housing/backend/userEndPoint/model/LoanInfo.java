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
 * Model definition for LoanInfo.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class LoanInfo extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double amount;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String bank;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String interestType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float rate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long time;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getAmount() {
    return amount;
  }

  /**
   * @param amount amount or {@code null} for none
   */
  public LoanInfo setAmount(java.lang.Double amount) {
    this.amount = amount;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getBank() {
    return bank;
  }

  /**
   * @param bank bank or {@code null} for none
   */
  public LoanInfo setBank(java.lang.String bank) {
    this.bank = bank;
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
  public LoanInfo setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getInterestType() {
    return interestType;
  }

  /**
   * @param interestType interestType or {@code null} for none
   */
  public LoanInfo setInterestType(java.lang.String interestType) {
    this.interestType = interestType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getRate() {
    return rate;
  }

  /**
   * @param rate rate or {@code null} for none
   */
  public LoanInfo setRate(java.lang.Float rate) {
    this.rate = rate;
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
  public LoanInfo setTime(java.lang.Long time) {
    this.time = time;
    return this;
  }

  @Override
  public LoanInfo set(String fieldName, Object value) {
    return (LoanInfo) super.set(fieldName, value);
  }

  @Override
  public LoanInfo clone() {
    return (LoanInfo) super.clone();
  }

}
