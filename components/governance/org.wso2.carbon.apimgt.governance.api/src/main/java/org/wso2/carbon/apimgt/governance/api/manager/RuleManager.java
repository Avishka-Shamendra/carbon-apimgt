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

package org.wso2.carbon.apimgt.governance.api.manager;

import org.wso2.carbon.apimgt.governance.api.error.GovernanceException;
import org.wso2.carbon.apimgt.governance.api.model.Rule;
import org.wso2.carbon.apimgt.governance.api.model.RuleInfo;
import org.wso2.carbon.apimgt.governance.api.model.RuleList;

/**
 * This interface represents the Governance Rule Manager
 */
public interface RuleManager {

    /**
     * Create a new Rule
     *
     * @param organization organization
     * @param rule         Rule
     * @return Rule
     * @throws GovernanceException if an error occurs while creating the Rule
     */
    Rule createNewRule(String organization, Rule rule) throws GovernanceException;

    /**
     * Get all the Rules
     *
     * @param organization organization
     * @return RuleList
     * @throws GovernanceException if an error occurs while getting the Rules
     */
    RuleList getRules(String organization) throws GovernanceException;

    /**
     * Get a Rule by Id
     *
     * @param organization organization
     * @param ruleId       Rule Id
     * @return RuleInfo
     * @throws GovernanceException if an error occurs while getting the Rule
     */
    RuleInfo getRuleById(String organization, String ruleId) throws GovernanceException;

    /**
     * Delete a Rule
     *
     * @param organization organization
     * @param ruleId       Rule Id
     * @throws GovernanceException if an error occurs while deleting the Rule
     */
    void deleteRule(String organization, String ruleId) throws GovernanceException;

    /**
     * Update a Rule
     *
     * @param organization organization
     * @param ruleId       Rule Id
     * @param rule         Rule
     * @return Rule
     * @throws GovernanceException if an error occurs while updating the Rule
     */
    Rule updateRule(String organization, String ruleId, Rule rule) throws GovernanceException;
}
