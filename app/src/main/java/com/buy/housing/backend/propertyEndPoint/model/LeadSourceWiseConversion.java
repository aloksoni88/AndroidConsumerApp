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
 * Model definition for LeadSourceWiseConversion.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class LeadSourceWiseConversion extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer bookingCount;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer leadNIAtSM;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer qualifiedAtRM;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer totalLead;

  /**
   * @return value or {@code null} for none
   */
  public Integer getBookingCount() {
    return bookingCount;
  }

  /**
   * @param bookingCount bookingCount or {@code null} for none
   */
  public LeadSourceWiseConversion setBookingCount(Integer bookingCount) {
    this.bookingCount = bookingCount;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getLeadNIAtSM() {
    return leadNIAtSM;
  }

  /**
   * @param leadNIAtSM leadNIAtSM or {@code null} for none
   */
  public LeadSourceWiseConversion setLeadNIAtSM(Integer leadNIAtSM) {
    this.leadNIAtSM = leadNIAtSM;
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
  public LeadSourceWiseConversion setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getQualifiedAtRM() {
    return qualifiedAtRM;
  }

  /**
   * @param qualifiedAtRM qualifiedAtRM or {@code null} for none
   */
  public LeadSourceWiseConversion setQualifiedAtRM(Integer qualifiedAtRM) {
    this.qualifiedAtRM = qualifiedAtRM;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getTotalLead() {
    return totalLead;
  }

  /**
   * @param totalLead totalLead or {@code null} for none
   */
  public LeadSourceWiseConversion setTotalLead(Integer totalLead) {
    this.totalLead = totalLead;
    return this;
  }

  @Override
  public LeadSourceWiseConversion set(String fieldName, Object value) {
    return (LeadSourceWiseConversion) super.set(fieldName, value);
  }

  @Override
  public LeadSourceWiseConversion clone() {
    return (LeadSourceWiseConversion) super.clone();
  }

}