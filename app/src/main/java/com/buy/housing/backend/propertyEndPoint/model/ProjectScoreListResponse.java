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
 * Model definition for ProjectScoreListResponse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ProjectScoreListResponse extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer count;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer errorId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String errorMessage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<ProjectScore> projectScoreList;

  static {
    // hack to force ProGuard to consider ProjectScore used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(ProjectScore.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean status;

  /**
   * @return value or {@code null} for none
   */
  public Integer getCount() {
    return count;
  }

  /**
   * @param count count or {@code null} for none
   */
  public ProjectScoreListResponse setCount(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getErrorId() {
    return errorId;
  }

  /**
   * @param errorId errorId or {@code null} for none
   */
  public ProjectScoreListResponse setErrorId(Integer errorId) {
    this.errorId = errorId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @param errorMessage errorMessage or {@code null} for none
   */
  public ProjectScoreListResponse setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
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
  public ProjectScoreListResponse setId(Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<ProjectScore> getProjectScoreList() {
    return projectScoreList;
  }

  /**
   * @param projectScoreList projectScoreList or {@code null} for none
   */
  public ProjectScoreListResponse setProjectScoreList(java.util.List<ProjectScore> projectScoreList) {
    this.projectScoreList = projectScoreList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Boolean getStatus() {
    return status;
  }

  /**
   * @param status status or {@code null} for none
   */
  public ProjectScoreListResponse setStatus(Boolean status) {
    this.status = status;
    return this;
  }

  @Override
  public ProjectScoreListResponse set(String fieldName, Object value) {
    return (ProjectScoreListResponse) super.set(fieldName, value);
  }

  @Override
  public ProjectScoreListResponse clone() {
    return (ProjectScoreListResponse) super.clone();
  }

}