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
 * Model definition for GetProjectDetailResponse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class GetProjectDetailResponse extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Agent> agentList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Block> blockList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<BlockUnitDetailResponse> blockUnitDetailResponseList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<BSPHistory> bspHistoryList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Builder builder;

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
  @com.google.api.client.util.Key
  private Integer inbondLeadCount;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<LeadAllocation> leadAllocationList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private LeadSourceWiseResponse leadSourceWiseResponse;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Project project;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer projectScore;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<ProjectStat> projectStat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<ProjectStatsMonthWise> projectStatsMonthWiseList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<PropertyType> propertyTypeList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer siteVisitScheduleCount;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Integer soldCount;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Boolean status;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Task task;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private Long updateTime;

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Agent> getAgentList() {
    return agentList;
  }

  /**
   * @param agentList agentList or {@code null} for none
   */
  public GetProjectDetailResponse setAgentList(java.util.List<Agent> agentList) {
    this.agentList = agentList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Block> getBlockList() {
    return blockList;
  }

  /**
   * @param blockList blockList or {@code null} for none
   */
  public GetProjectDetailResponse setBlockList(java.util.List<Block> blockList) {
    this.blockList = blockList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<BlockUnitDetailResponse> getBlockUnitDetailResponseList() {
    return blockUnitDetailResponseList;
  }

  /**
   * @param blockUnitDetailResponseList blockUnitDetailResponseList or {@code null} for none
   */
  public GetProjectDetailResponse setBlockUnitDetailResponseList(java.util.List<BlockUnitDetailResponse> blockUnitDetailResponseList) {
    this.blockUnitDetailResponseList = blockUnitDetailResponseList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<BSPHistory> getBspHistoryList() {
    return bspHistoryList;
  }

  /**
   * @param bspHistoryList bspHistoryList or {@code null} for none
   */
  public GetProjectDetailResponse setBspHistoryList(java.util.List<BSPHistory> bspHistoryList) {
    this.bspHistoryList = bspHistoryList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Builder getBuilder() {
    return builder;
  }

  /**
   * @param builder builder or {@code null} for none
   */
  public GetProjectDetailResponse setBuilder(Builder builder) {
    this.builder = builder;
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
  public GetProjectDetailResponse setErrorId(Integer errorId) {
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
  public GetProjectDetailResponse setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getInbondLeadCount() {
    return inbondLeadCount;
  }

  /**
   * @param inbondLeadCount inbondLeadCount or {@code null} for none
   */
  public GetProjectDetailResponse setInbondLeadCount(Integer inbondLeadCount) {
    this.inbondLeadCount = inbondLeadCount;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<LeadAllocation> getLeadAllocationList() {
    return leadAllocationList;
  }

  /**
   * @param leadAllocationList leadAllocationList or {@code null} for none
   */
  public GetProjectDetailResponse setLeadAllocationList(java.util.List<LeadAllocation> leadAllocationList) {
    this.leadAllocationList = leadAllocationList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public LeadSourceWiseResponse getLeadSourceWiseResponse() {
    return leadSourceWiseResponse;
  }

  /**
   * @param leadSourceWiseResponse leadSourceWiseResponse or {@code null} for none
   */
  public GetProjectDetailResponse setLeadSourceWiseResponse(LeadSourceWiseResponse leadSourceWiseResponse) {
    this.leadSourceWiseResponse = leadSourceWiseResponse;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Project getProject() {
    return project;
  }

  /**
   * @param project project or {@code null} for none
   */
  public GetProjectDetailResponse setProject(Project project) {
    this.project = project;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getProjectScore() {
    return projectScore;
  }

  /**
   * @param projectScore projectScore or {@code null} for none
   */
  public GetProjectDetailResponse setProjectScore(Integer projectScore) {
    this.projectScore = projectScore;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<ProjectStat> getProjectStat() {
    return projectStat;
  }

  /**
   * @param projectStat projectStat or {@code null} for none
   */
  public GetProjectDetailResponse setProjectStat(java.util.List<ProjectStat> projectStat) {
    this.projectStat = projectStat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<ProjectStatsMonthWise> getProjectStatsMonthWiseList() {
    return projectStatsMonthWiseList;
  }

  /**
   * @param projectStatsMonthWiseList projectStatsMonthWiseList or {@code null} for none
   */
  public GetProjectDetailResponse setProjectStatsMonthWiseList(java.util.List<ProjectStatsMonthWise> projectStatsMonthWiseList) {
    this.projectStatsMonthWiseList = projectStatsMonthWiseList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<PropertyType> getPropertyTypeList() {
    return propertyTypeList;
  }

  /**
   * @param propertyTypeList propertyTypeList or {@code null} for none
   */
  public GetProjectDetailResponse setPropertyTypeList(java.util.List<PropertyType> propertyTypeList) {
    this.propertyTypeList = propertyTypeList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getSiteVisitScheduleCount() {
    return siteVisitScheduleCount;
  }

  /**
   * @param siteVisitScheduleCount siteVisitScheduleCount or {@code null} for none
   */
  public GetProjectDetailResponse setSiteVisitScheduleCount(Integer siteVisitScheduleCount) {
    this.siteVisitScheduleCount = siteVisitScheduleCount;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Integer getSoldCount() {
    return soldCount;
  }

  /**
   * @param soldCount soldCount or {@code null} for none
   */
  public GetProjectDetailResponse setSoldCount(Integer soldCount) {
    this.soldCount = soldCount;
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
  public GetProjectDetailResponse setStatus(Boolean status) {
    this.status = status;
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
  public GetProjectDetailResponse setTask(Task task) {
    this.task = task;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Long getUpdateTime() {
    return updateTime;
  }

  /**
   * @param updateTime updateTime or {@code null} for none
   */
  public GetProjectDetailResponse setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  @Override
  public GetProjectDetailResponse set(String fieldName, Object value) {
    return (GetProjectDetailResponse) super.set(fieldName, value);
  }

  @Override
  public GetProjectDetailResponse clone() {
    return (GetProjectDetailResponse) super.clone();
  }

}
