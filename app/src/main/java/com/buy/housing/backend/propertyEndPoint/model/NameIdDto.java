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
 * Model definition for NameIdDto.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class NameIdDto extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String city;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String name;

  /**
   * @return value or {@code null} for none
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city city or {@code null} for none
   */
  public NameIdDto setCity(String city) {
    this.city = city;
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
  public NameIdDto setId(Long id) {
    this.id = id;
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
  public NameIdDto setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public NameIdDto set(String fieldName, Object value) {
    return (NameIdDto) super.set(fieldName, value);
  }

  @Override
  public NameIdDto clone() {
    return (NameIdDto) super.clone();
  }

}
