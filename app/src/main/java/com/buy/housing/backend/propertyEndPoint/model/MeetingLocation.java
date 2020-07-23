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
 * Model definition for MeetingLocation.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class MeetingLocation extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String address;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Double lat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Double lng;

  /**
   * @return value or {@code null} for none
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address address or {@code null} for none
   */
  public MeetingLocation setAddress(String address) {
    this.address = address;
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
  public MeetingLocation setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Double getLat() {
    return lat;
  }

  /**
   * @param lat lat or {@code null} for none
   */
  public MeetingLocation setLat(Double lat) {
    this.lat = lat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Double getLng() {
    return lng;
  }

  /**
   * @param lng lng or {@code null} for none
   */
  public MeetingLocation setLng(Double lng) {
    this.lng = lng;
    return this;
  }

  @Override
  public MeetingLocation set(String fieldName, Object value) {
    return (MeetingLocation) super.set(fieldName, value);
  }

  @Override
  public MeetingLocation clone() {
    return (MeetingLocation) super.clone();
  }

}