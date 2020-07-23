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
 * Model definition for AgentCheckInHistory.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the loanEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class AgentCheckInHistory extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long agentId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String agentName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean checkIn;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String description;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double lat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double lng;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean manual;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long projectId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String projectName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long time;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getAgentId() {
    return agentId;
  }

  /**
   * @param agentId agentId or {@code null} for none
   */
  public AgentCheckInHistory setAgentId(java.lang.Long agentId) {
    this.agentId = agentId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAgentName() {
    return agentName;
  }

  /**
   * @param agentName agentName or {@code null} for none
   */
  public AgentCheckInHistory setAgentName(java.lang.String agentName) {
    this.agentName = agentName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getCheckIn() {
    return checkIn;
  }

  /**
   * @param checkIn checkIn or {@code null} for none
   */
  public AgentCheckInHistory setCheckIn(java.lang.Boolean checkIn) {
    this.checkIn = checkIn;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * @param description description or {@code null} for none
   */
  public AgentCheckInHistory setDescription(java.lang.String description) {
    this.description = description;
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
  public AgentCheckInHistory setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getLat() {
    return lat;
  }

  /**
   * @param lat lat or {@code null} for none
   */
  public AgentCheckInHistory setLat(java.lang.Double lat) {
    this.lat = lat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getLng() {
    return lng;
  }

  /**
   * @param lng lng or {@code null} for none
   */
  public AgentCheckInHistory setLng(java.lang.Double lng) {
    this.lng = lng;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getManual() {
    return manual;
  }

  /**
   * @param manual manual or {@code null} for none
   */
  public AgentCheckInHistory setManual(java.lang.Boolean manual) {
    this.manual = manual;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getProjectId() {
    return projectId;
  }

  /**
   * @param projectId projectId or {@code null} for none
   */
  public AgentCheckInHistory setProjectId(java.lang.Long projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getProjectName() {
    return projectName;
  }

  /**
   * @param projectName projectName or {@code null} for none
   */
  public AgentCheckInHistory setProjectName(java.lang.String projectName) {
    this.projectName = projectName;
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
  public AgentCheckInHistory setTime(java.lang.Long time) {
    this.time = time;
    return this;
  }

  @Override
  public AgentCheckInHistory set(String fieldName, Object value) {
    return (AgentCheckInHistory) super.set(fieldName, value);
  }

  @Override
  public AgentCheckInHistory clone() {
    return (AgentCheckInHistory) super.clone();
  }

}
