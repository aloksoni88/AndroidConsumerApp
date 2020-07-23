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
 * on 2017-09-25 at 11:50:49 UTC 
 * Modify at your own risk.
 */

package com.buy.housing.backend.taskEndPoint.model;

/**
 * Model definition for GetTaskDetailResponse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the taskEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class GetTaskDetailResponse extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Lead lead;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Task task;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Task> taskList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private User user;

  /**
   * @return value or {@code null} for none
   */
  public Lead getLead() {
    return lead;
  }

  /**
   * @param lead lead or {@code null} for none
   */
  public GetTaskDetailResponse setLead(Lead lead) {
    this.lead = lead;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Task getTask() {
    return task;
  }

  /**
   * @param task task or {@code null} for none
   */
  public GetTaskDetailResponse setTask(Task task) {
    this.task = task;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Task> getTaskList() {
    return taskList;
  }

  /**
   * @param taskList taskList or {@code null} for none
   */
  public GetTaskDetailResponse setTaskList(java.util.List<Task> taskList) {
    this.taskList = taskList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user user or {@code null} for none
   */
  public GetTaskDetailResponse setUser(User user) {
    this.user = user;
    return this;
  }

  @Override
  public GetTaskDetailResponse set(String fieldName, Object value) {
    return (GetTaskDetailResponse) super.set(fieldName, value);
  }

  @Override
  public GetTaskDetailResponse clone() {
    return (GetTaskDetailResponse) super.clone();
  }

}
