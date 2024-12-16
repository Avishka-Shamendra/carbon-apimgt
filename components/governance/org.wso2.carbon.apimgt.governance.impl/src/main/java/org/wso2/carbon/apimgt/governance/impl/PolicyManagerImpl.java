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

package org.wso2.carbon.apimgt.governance.impl;

import org.wso2.carbon.apimgt.governance.api.error.GovernanceException;
import org.wso2.carbon.apimgt.governance.api.error.GovernanceExceptionCodes;
import org.wso2.carbon.apimgt.governance.api.manager.PolicyManager;
import org.wso2.carbon.apimgt.governance.api.model.GovernancePolicy;
import org.wso2.carbon.apimgt.governance.api.model.GovernancePolicyInfoWithRulesetIds;
import org.wso2.carbon.apimgt.governance.api.model.GovernancePolicyList;
import org.wso2.carbon.apimgt.governance.impl.dao.GovernancePolicyMgtDAO;
import org.wso2.carbon.apimgt.governance.impl.dao.impl.GovernancePolicyMgtDAOImpl;
import org.wso2.carbon.apimgt.governance.impl.util.GovernanceUtil;

/**
 * This class represents the Governance Policy Manager Implementation
 */
public class PolicyManagerImpl implements PolicyManager {

    private GovernancePolicyMgtDAO policyMgtDAO;

    public PolicyManagerImpl() {
        policyMgtDAO = GovernancePolicyMgtDAOImpl.getInstance();
    }

    /**
     * Create a new Governance Policy
     *
     * @param organization                       Organization
     * @param governancePolicyInfoWithRulesetIds Governance Policy Info with Ruleset Ids
     * @return GovernancePolicyInfo Created object
     * @throws GovernanceException If an error occurs while creating the policy
     */
    @Override
    public GovernancePolicy createGovernancePolicy(String organization, GovernancePolicyInfoWithRulesetIds
            governancePolicyInfoWithRulesetIds) throws GovernanceException {
        governancePolicyInfoWithRulesetIds.setId(GovernanceUtil.generateUUID());
        return policyMgtDAO.createGovernancePolicy(organization, governancePolicyInfoWithRulesetIds);
    }

    /**
     * Get Governance Policy by Name
     *
     * @param organization Organization
     * @param policyID     Policy ID
     * @return GovernancePolicyInfo
     * @throws GovernanceException If an error occurs while retrieving the policy
     */
    @Override
    public GovernancePolicy getGovernancePolicyByID(String organization, String policyID) throws GovernanceException {
        GovernancePolicy policyInfo = policyMgtDAO.getGovernancePolicyByID(organization, policyID);
        if (policyInfo == null) {
            throw new GovernanceException(GovernanceExceptionCodes.POLICY_NOT_FOUND, policyID, organization);
        }
        return policyInfo;
    }

    /**
     * Get Governance Policies
     *
     * @param organization Organization
     * @return GovernancePolicyList
     * @throws GovernanceException If an error occurs while retrieving the policies
     */
    @Override
    public GovernancePolicyList getGovernancePolicies(String organization) throws GovernanceException {
        return policyMgtDAO.getGovernancePolicies(organization);
    }

    /**
     * Delete a Governance Policy
     *
     * @param policyId     Policy ID
     * @param organization Organization
     * @throws GovernanceException If an error occurs while deleting the policy
     */
    @Override
    public void deletePolicy(String policyId, String organization) throws GovernanceException {
        policyMgtDAO.deletePolicy(policyId, organization);
    }

    /**
     * Update a Governance Policy
     *
     * @param policyId                           Policy ID
     * @param organization                       Organization
     * @param governancePolicyInfoWithRulesetIds Governance Policy Info with Ruleset Ids
     * @return GovernancePolicyInfo Updated object
     * @throws GovernanceException If an error occurs while updating the policy
     */
    @Override
    public GovernancePolicy updateGovernancePolicy(String policyId, String organization,
                                                   GovernancePolicyInfoWithRulesetIds governancePolicyInfoWithRulesetIds)
            throws GovernanceException {
        return policyMgtDAO.updateGovernancePolicy(policyId, organization, governancePolicyInfoWithRulesetIds);
    }
}
