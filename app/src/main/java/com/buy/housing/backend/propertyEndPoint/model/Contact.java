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
 * Model definition for Contact.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Contact extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String email;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String extension;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String fax;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean internal;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String landLine;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String phone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String role;

  /**
   * @return value or {@code null} for none
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email email or {@code null} for none
   */
  public Contact setEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getExtension() {
    return extension;
  }

  /**
   * @param extension extension or {@code null} for none
   */
  public Contact setExtension(String extension) {
    this.extension = extension;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getFax() {
    return fax;
  }

  /**
   * @param fax fax or {@code null} for none
   */
  public Contact setFax(String fax) {
    this.fax = fax;
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
  public Contact setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getInternal() {
    return internal;
  }

  /**
   * @param internal internal or {@code null} for none
   */
  public Contact setInternal(Boolean internal) {
    this.internal = internal;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getLandLine() {
    return landLine;
  }

  /**
   * @param landLine landLine or {@code null} for none
   */
  public Contact setLandLine(String landLine) {
    this.landLine = landLine;
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
  public Contact setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getPhone() {
    return phone;
  }

  /**
   * @param phone phone or {@code null} for none
   */
  public Contact setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getRole() {
    return role;
  }

  /**
   * @param role role or {@code null} for none
   */
  public Contact setRole(String role) {
    this.role = role;
    return this;
  }

  @Override
  public Contact set(String fieldName, Object value) {
    return (Contact) super.set(fieldName, value);
  }

  @Override
  public Contact clone() {
    return (Contact) super.clone();
  }

}