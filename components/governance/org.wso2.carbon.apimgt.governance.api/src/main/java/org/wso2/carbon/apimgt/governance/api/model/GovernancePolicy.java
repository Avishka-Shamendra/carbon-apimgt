/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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

import java.util.ArrayList;
import java.util.List;

public class GovernancePolicy {
    private String id;
    private String name;
    private String description;
    private List<String> ruleIds = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    private List<String> associatedStates = new ArrayList<>();
    private String actionOnViolation;
    private String createdBy;
    private String createdTime;
    private String updatedBy;
    private String updatedTime;

}