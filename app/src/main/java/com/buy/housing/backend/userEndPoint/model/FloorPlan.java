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
 * Model definition for FloorPlan.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class FloorPlan extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String surl2d;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String surl3d;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String surlImage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String url2d;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String url360;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String url3d;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String urlImage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String urlKML;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String urlVideo;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public FloorPlan setId(java.lang.Long id) {
    this.id = id;
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
  public FloorPlan setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSurl2d() {
    return surl2d;
  }

  /**
   * @param surl2d surl2d or {@code null} for none
   */
  public FloorPlan setSurl2d(java.lang.String surl2d) {
    this.surl2d = surl2d;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSurl3d() {
    return surl3d;
  }

  /**
   * @param surl3d surl3d or {@code null} for none
   */
  public FloorPlan setSurl3d(java.lang.String surl3d) {
    this.surl3d = surl3d;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSurlImage() {
    return surlImage;
  }

  /**
   * @param surlImage surlImage or {@code null} for none
   */
  public FloorPlan setSurlImage(java.lang.String surlImage) {
    this.surlImage = surlImage;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrl2d() {
    return url2d;
  }

  /**
   * @param url2d url2d or {@code null} for none
   */
  public FloorPlan setUrl2d(java.lang.String url2d) {
    this.url2d = url2d;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrl360() {
    return url360;
  }

  /**
   * @param url360 url360 or {@code null} for none
   */
  public FloorPlan setUrl360(java.lang.String url360) {
    this.url360 = url360;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrl3d() {
    return url3d;
  }

  /**
   * @param url3d url3d or {@code null} for none
   */
  public FloorPlan setUrl3d(java.lang.String url3d) {
    this.url3d = url3d;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrlImage() {
    return urlImage;
  }

  /**
   * @param urlImage urlImage or {@code null} for none
   */
  public FloorPlan setUrlImage(java.lang.String urlImage) {
    this.urlImage = urlImage;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrlKML() {
    return urlKML;
  }

  /**
   * @param urlKML urlKML or {@code null} for none
   */
  public FloorPlan setUrlKML(java.lang.String urlKML) {
    this.urlKML = urlKML;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUrlVideo() {
    return urlVideo;
  }

  /**
   * @param urlVideo urlVideo or {@code null} for none
   */
  public FloorPlan setUrlVideo(java.lang.String urlVideo) {
    this.urlVideo = urlVideo;
    return this;
  }

  @Override
  public FloorPlan set(String fieldName, Object value) {
    return (FloorPlan) super.set(fieldName, value);
  }

  @Override
  public FloorPlan clone() {
    return (FloorPlan) super.clone();
  }

}
