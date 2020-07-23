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
 * on 2020-02-28 at 09:14:15 UTC 
 * Modify at your own risk.
 */

package com.buy.housing.backend.estimateEndPoint.model;

/**
 * Model definition for AgentCC.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the estimateEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class AgentCC extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long agentId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String agentType;

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
   * @return value or {@code null} for none
   */
  public java.lang.Long getAgentId() {
    return agentId;
  }

  /**
   * @param agentId agentId or {@code null} for none
   */
  public AgentCC setAgentId(java.lang.Long agentId) {
    this.agentId = agentId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAgentType() {
    return agentType;
  }

  /**
   * @param agentType agentType or {@code null} for none
   */
  public AgentCC setAgentType(java.lang.String agentType) {
    this.agentType = agentType;
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
  public AgentCC setId(java.lang.Long id) {
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
  public AgentCC setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  @Override
  public AgentCC set(String fieldName, Object value) {
    return (AgentCC) super.set(fieldName, value);
  }

  @Override
  public AgentCC clone() {
    return (AgentCC) super.clone();
  }

}
