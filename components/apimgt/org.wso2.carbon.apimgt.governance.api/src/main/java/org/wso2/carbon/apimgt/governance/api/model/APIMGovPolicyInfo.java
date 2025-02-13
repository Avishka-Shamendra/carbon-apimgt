/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.governance.api.model;

/**
 * This class represents a governance policy information excluding the policy content
 */
public class APIMGovPolicyInfo {
    private String id;
    private String name;
    private String description;
    private APIMGovPolicyCategory policyCategory;
    private APIMGovPolicyType policyType;
    private ExtendedArtifactType artifactType;
    private String documentationLink;
    private String provider;
    private String createdBy;
    private String createdTime;
    private String updatedBy;
    private String updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public APIMGovPolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(APIMGovPolicyType policyType) {
        this.policyType = policyType;
    }

    public ExtendedArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ExtendedArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    public String getDocumentationLink() {
        return documentationLink;
    }

    public void setDocumentationLink(String documentationLink) {
        this.documentationLink = documentationLink;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public APIMGovPolicyCategory getPolicyCategory() {
        return policyCategory;
    }

    public void setPolicyCategory(APIMGovPolicyCategory policyCategory) {
        this.policyCategory = policyCategory;
    }
}
