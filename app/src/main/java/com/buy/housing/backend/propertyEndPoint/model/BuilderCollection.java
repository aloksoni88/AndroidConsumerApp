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
 * Model definition for BuilderCollection.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the propertyEndPoint. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class BuilderCollection extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Builder> items;

  static {
    // hack to force ProGuard to consider Builder used, since otherwise it would be stripped out
    // see https://github.com/google/google-api-java-client/issues/543
    com.google.api.client.util.Data.nullOf(Builder.class);
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Builder> getItems() {
    return items;
  }

  /**
   * @param items items or {@code null} for none
   */
  public BuilderCollection setItems(java.util.List<Builder> items) {
    this.items = items;
    return this;
  }

  @Override
  public BuilderCollection set(String fieldName, Object value) {
    return (BuilderCollection) super.set(fieldName, value);
  }

  @Override
  public BuilderCollection clone() {
    return (BuilderCollection) super.clone();
  }

}
