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
 * Model definition for NewsResponse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class NewsResponse extends com.google.api.client.json.GenericJson {

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
  private News newsObj;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean status;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getErrorId() {
    return errorId;
  }

  /**
   * @param errorId errorId or {@code null} for none
   */
  public NewsResponse setErrorId(java.lang.Integer errorId) {
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
  public NewsResponse setErrorMessage(java.lang.String errorMessage) {
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
  public NewsResponse setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public News getNewsObj() {
    return newsObj;
  }

  /**
   * @param newsObj newsObj or {@code null} for none
   */
  public NewsResponse setNewsObj(News newsObj) {
    this.newsObj = newsObj;
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
  public NewsResponse setStatus(java.lang.Boolean status) {
    this.status = status;
    return this;
  }

  @Override
  public NewsResponse set(String fieldName, Object value) {
    return (NewsResponse) super.set(fieldName, value);
  }

  @Override
  public NewsResponse clone() {
    return (NewsResponse) super.clone();
  }

}