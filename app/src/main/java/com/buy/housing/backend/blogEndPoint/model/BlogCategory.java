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
 * (build: 2017-09-05 21:06:36 UTC)
 * on 2017-09-18 at 08:15:41 UTC 
 * Modify at your own risk.
 */

package com.buy.housing.backend.blogEndPoint.model;

/**
 * Model definition for BlogCategory.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the blogEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class BlogCategory extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long createById;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String createByName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long createTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String desc;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String searchName;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreateById() {
    return createById;
  }

  /**
   * @param createById createById or {@code null} for none
   */
  public BlogCategory setCreateById(java.lang.Long createById) {
    this.createById = createById;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCreateByName() {
    return createByName;
  }

  /**
   * @param createByName createByName or {@code null} for none
   */
  public BlogCategory setCreateByName(java.lang.String createByName) {
    this.createByName = createByName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreateTime() {
    return createTime;
  }

  /**
   * @param createTime createTime or {@code null} for none
   */
  public BlogCategory setCreateTime(java.lang.Long createTime) {
    this.createTime = createTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDesc() {
    return desc;
  }

  /**
   * @param desc desc or {@code null} for none
   */
  public BlogCategory setDesc(java.lang.String desc) {
    this.desc = desc;
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
  public BlogCategory setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSearchName() {
    return searchName;
  }

  /**
   * @param searchName searchName or {@code null} for none
   */
  public BlogCategory setSearchName(java.lang.String searchName) {
    this.searchName = searchName;
    return this;
  }

  @Override
  public BlogCategory set(String fieldName, Object value) {
    return (BlogCategory) super.set(fieldName, value);
  }

  @Override
  public BlogCategory clone() {
    return (BlogCategory) super.clone();
  }

}