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
 * Model definition for Task.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Task extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<AgentCC> agentCCList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<AgentCheckInHistory> agentCheckInHistory;

  static {
    // hack to force ProGuard to consider AgentCheckInHistory used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(AgentCheckInHistory.class);
  }

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<NameIdDto> agentList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String agentPath;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long assignedId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String assignedName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long bookingId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long builderId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<CallHistory> callHistory;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String city;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Comment> commentList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long createTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long creatorId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String creatorName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String description;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private MeetingLocation doneLocation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long doneTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long endTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long lastRescheduleTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long leadId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String leadName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String leadPhone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private MeetingLocation meetingLocation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long ownerId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String ownerName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String priority;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long projectId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<NameIdDto> projectList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String projectName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long reminderTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long requestTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String serviceRequestType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long startTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String status;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String taskCategory;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String taskType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String taskView;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<TimeLineStats> timeLineStates;

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<AgentCC> getAgentCCList() {
    return agentCCList;
  }

  /**
   * @param agentCCList agentCCList or {@code null} for none
   */
  public Task setAgentCCList(java.util.List<AgentCC> agentCCList) {
    this.agentCCList = agentCCList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<AgentCheckInHistory> getAgentCheckInHistory() {
    return agentCheckInHistory;
  }

  /**
   * @param agentCheckInHistory agentCheckInHistory or {@code null} for none
   */
  public Task setAgentCheckInHistory(java.util.List<AgentCheckInHistory> agentCheckInHistory) {
    this.agentCheckInHistory = agentCheckInHistory;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<NameIdDto> getAgentList() {
    return agentList;
  }

  /**
   * @param agentList agentList or {@code null} for none
   */
  public Task setAgentList(java.util.List<NameIdDto> agentList) {
    this.agentList = agentList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getAgentPath() {
    return agentPath;
  }

  /**
   * @param agentPath agentPath or {@code null} for none
   */
  public Task setAgentPath(String agentPath) {
    this.agentPath = agentPath;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getAssignedId() {
    return assignedId;
  }

  /**
   * @param assignedId assignedId or {@code null} for none
   */
  public Task setAssignedId(Long assignedId) {
    this.assignedId = assignedId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getAssignedName() {
    return assignedName;
  }

  /**
   * @param assignedName assignedName or {@code null} for none
   */
  public Task setAssignedName(String assignedName) {
    this.assignedName = assignedName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getBookingId() {
    return bookingId;
  }

  /**
   * @param bookingId bookingId or {@code null} for none
   */
  public Task setBookingId(Long bookingId) {
    this.bookingId = bookingId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getBuilderId() {
    return builderId;
  }

  /**
   * @param builderId builderId or {@code null} for none
   */
  public Task setBuilderId(Long builderId) {
    this.builderId = builderId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<CallHistory> getCallHistory() {
    return callHistory;
  }

  /**
   * @param callHistory callHistory or {@code null} for none
   */
  public Task setCallHistory(java.util.List<CallHistory> callHistory) {
    this.callHistory = callHistory;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city city or {@code null} for none
   */
  public Task setCity(String city) {
    this.city = city;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Comment> getCommentList() {
    return commentList;
  }

  /**
   * @param commentList commentList or {@code null} for none
   */
  public Task setCommentList(java.util.List<Comment> commentList) {
    this.commentList = commentList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getCreateTime() {
    return createTime;
  }

  /**
   * @param createTime createTime or {@code null} for none
   */
  public Task setCreateTime(Long createTime) {
    this.createTime = createTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getCreatorId() {
    return creatorId;
  }

  /**
   * @param creatorId creatorId or {@code null} for none
   */
  public Task setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getCreatorName() {
    return creatorName;
  }

  /**
   * @param creatorName creatorName or {@code null} for none
   */
  public Task setCreatorName(String creatorName) {
    this.creatorName = creatorName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description description or {@code null} for none
   */
  public Task setDescription(String description) {
    this.description = description;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public MeetingLocation getDoneLocation() {
    return doneLocation;
  }

  /**
   * @param doneLocation doneLocation or {@code null} for none
   */
  public Task setDoneLocation(MeetingLocation doneLocation) {
    this.doneLocation = doneLocation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getDoneTime() {
    return doneTime;
  }

  /**
   * @param doneTime doneTime or {@code null} for none
   */
  public Task setDoneTime(Long doneTime) {
    this.doneTime = doneTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getEndTime() {
    return endTime;
  }

  /**
   * @param endTime endTime or {@code null} for none
   */
  public Task setEndTime(Long endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Task setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getLastRescheduleTime() {
    return lastRescheduleTime;
  }

  /**
   * @param lastRescheduleTime lastRescheduleTime or {@code null} for none
   */
  public Task setLastRescheduleTime(Long lastRescheduleTime) {
    this.lastRescheduleTime = lastRescheduleTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getLeadId() {
    return leadId;
  }

  /**
   * @param leadId leadId or {@code null} for none
   */
  public Task setLeadId(Long leadId) {
    this.leadId = leadId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getLeadName() {
    return leadName;
  }

  /**
   * @param leadName leadName or {@code null} for none
   */
  public Task setLeadName(String leadName) {
    this.leadName = leadName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getLeadPhone() {
    return leadPhone;
  }

  /**
   * @param leadPhone leadPhone or {@code null} for none
   */
  public Task setLeadPhone(String leadPhone) {
    this.leadPhone = leadPhone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public MeetingLocation getMeetingLocation() {
    return meetingLocation;
  }

  /**
   * @param meetingLocation meetingLocation or {@code null} for none
   */
  public Task setMeetingLocation(MeetingLocation meetingLocation) {
    this.meetingLocation = meetingLocation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getOwnerId() {
    return ownerId;
  }

  /**
   * @param ownerId ownerId or {@code null} for none
   */
  public Task setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * @param ownerName ownerName or {@code null} for none
   */
  public Task setOwnerName(String ownerName) {
    this.ownerName = ownerName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getPriority() {
    return priority;
  }

  /**
   * @param priority priority or {@code null} for none
   */
  public Task setPriority(String priority) {
    this.priority = priority;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getProjectId() {
    return projectId;
  }

  /**
   * @param projectId projectId or {@code null} for none
   */
  public Task setProjectId(Long projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<NameIdDto> getProjectList() {
    return projectList;
  }

  /**
   * @param projectList projectList or {@code null} for none
   */
  public Task setProjectList(java.util.List<NameIdDto> projectList) {
    this.projectList = projectList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * @param projectName projectName or {@code null} for none
   */
  public Task setProjectName(String projectName) {
    this.projectName = projectName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getReminderTime() {
    return reminderTime;
  }

  /**
   * @param reminderTime reminderTime or {@code null} for none
   */
  public Task setReminderTime(Long reminderTime) {
    this.reminderTime = reminderTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getRequestTime() {
    return requestTime;
  }

  /**
   * @param requestTime requestTime or {@code null} for none
   */
  public Task setRequestTime(Long requestTime) {
    this.requestTime = requestTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getServiceRequestType() {
    return serviceRequestType;
  }

  /**
   * @param serviceRequestType serviceRequestType or {@code null} for none
   */
  public Task setServiceRequestType(String serviceRequestType) {
    this.serviceRequestType = serviceRequestType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getStartTime() {
    return startTime;
  }

  /**
   * @param startTime startTime or {@code null} for none
   */
  public Task setStartTime(Long startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status status or {@code null} for none
   */
  public Task setStatus(String status) {
    this.status = status;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getTaskCategory() {
    return taskCategory;
  }

  /**
   * @param taskCategory taskCategory or {@code null} for none
   */
  public Task setTaskCategory(String taskCategory) {
    this.taskCategory = taskCategory;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getTaskType() {
    return taskType;
  }

  /**
   * @param taskType taskType or {@code null} for none
   */
  public Task setTaskType(String taskType) {
    this.taskType = taskType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public String getTaskView() {
    return taskView;
  }

  /**
   * @param taskView taskView or {@code null} for none
   */
  public Task setTaskView(String taskView) {
    this.taskView = taskView;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<TimeLineStats> getTimeLineStates() {
    return timeLineStates;
  }

  /**
   * @param timeLineStates timeLineStates or {@code null} for none
   */
  public Task setTimeLineStates(java.util.List<TimeLineStats> timeLineStates) {
    this.timeLineStates = timeLineStates;
    return this;
  }

  @Override
  public Task set(String fieldName, Object value) {
    return (Task) super.set(fieldName, value);
  }

  @Override
  public Task clone() {
    return (Task) super.clone();
  }

}
