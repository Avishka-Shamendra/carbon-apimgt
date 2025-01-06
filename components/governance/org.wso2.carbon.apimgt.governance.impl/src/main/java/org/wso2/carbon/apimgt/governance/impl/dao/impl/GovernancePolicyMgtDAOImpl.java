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

package org.wso2.carbon.apimgt.governance.impl.dao.impl;

import org.wso2.carbon.apimgt.governance.api.error.GovernanceException;
import org.wso2.carbon.apimgt.governance.api.error.GovernanceExceptionCodes;
import org.wso2.carbon.apimgt.governance.api.model.GovernanceAction;
import org.wso2.carbon.apimgt.governance.api.model.GovernancePolicy;
import org.wso2.carbon.apimgt.governance.api.model.GovernancePolicyList;
import org.wso2.carbon.apimgt.governance.api.model.RulesetInfo;
import org.wso2.carbon.apimgt.governance.impl.dao.GovernancePolicyMgtDAO;
import org.wso2.carbon.apimgt.governance.impl.dao.constants.SQLConstants;
import org.wso2.carbon.apimgt.governance.impl.util.GovernanceDBUtil;
import org.wso2.carbon.apimgt.governance.impl.util.GovernanceUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the GovernancePolicyMgtDAO interface.
 */
public class GovernancePolicyMgtDAOImpl implements GovernancePolicyMgtDAO {

    private static GovernancePolicyMgtDAO INSTANCE = null;

    private GovernancePolicyMgtDAOImpl() {
    }

    /**
     * Get an instance of GovernancePolicyMgtDAO
     *
     * @return GovernancePolicyMgtDAO instance
     */
    public static GovernancePolicyMgtDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GovernancePolicyMgtDAOImpl();
        }
        return INSTANCE;
    }

    /**
     * Create a new Governance Policy
     *
     * @param organization     Organization
     * @param governancePolicy Governance Policy Info with Ruleset Ids
     * @return GovernancePolicy Created object
     */
    @Override
    public GovernancePolicy createGovernancePolicy(String organization, GovernancePolicy
            governancePolicy) throws GovernanceException {
        List<RulesetInfo> rulesetInfoList = new ArrayList<>();
        List<String> rulesetIds;
        Timestamp timestamp;
        List<String> labels;
        List<String> states;
        List<GovernanceAction> actions;
        try (Connection connection = GovernanceDBUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.CREATE_POLICY)) {
                    prepStmt.setString(1, governancePolicy.getId());
                    prepStmt.setString(2, governancePolicy.getName());
                    prepStmt.setString(3, governancePolicy.getDescription());
                    prepStmt.setString(4, organization);
                    prepStmt.setString(5, governancePolicy.getCreatedBy());

                    timestamp = new Timestamp(System.currentTimeMillis());
                    governancePolicy.setCreatedTime(timestamp.toString());
                    prepStmt.setTimestamp(6, timestamp);
                    prepStmt.execute();
                }
                // Insert into GOV_POLICY_RULESET_MAPPING table
                try (PreparedStatement prepStmt =
                             connection.prepareStatement(SQLConstants.CREATE_POLICY_RULESET_MAPPING)) {
                    rulesetIds = governancePolicy.getRulesetIds();
                    for (String rulesetId : rulesetIds) {
                        prepStmt.setString(1, GovernanceUtil.generateUUID());
                        prepStmt.setString(2, governancePolicy.getId());
                        prepStmt.setString(3, rulesetId);
                        prepStmt.addBatch();
                    }
                    prepStmt.executeBatch();
                }

                // Insert into GOV_POLICY_LABEL table
                try (PreparedStatement prepStmt =
                             connection.prepareStatement(SQLConstants.CREATE_GOVERNANCE_POLICY_LABEL_MAPPING)) {
                    labels = governancePolicy.getLabels();
                    for (String label : labels) {
                        prepStmt.setString(1, GovernanceUtil.generateUUID());
                        prepStmt.setString(2, governancePolicy.getId());
                        prepStmt.setString(3, label);
                        prepStmt.addBatch();
                    }
                    prepStmt.executeBatch();
                }

                // Insert into GOV_POLICY_APPLICABLE_STATE table
                try (PreparedStatement prepStmt =
                             connection.prepareStatement(SQLConstants.CREATE_GOVERNANCE_POLICY_STATE_MAPPING)) {
                    states = governancePolicy.getApplicableStates();
                    for (String state : states) {
                        prepStmt.setString(1, GovernanceUtil.generateUUID());
                        prepStmt.setString(2, governancePolicy.getId());
                        prepStmt.setString(3, state);
                        prepStmt.addBatch();
                    }
                    prepStmt.executeBatch();
                }

                // Insert into GOV_POLICY_ACTION table
                try (PreparedStatement prepStmt =
                             connection.prepareStatement(SQLConstants.CREATE_GOVERNANCE_POLICY_ACTION_MAPPING)) {
                    actions = governancePolicy.getActions();
                    for (GovernanceAction action : actions) {
                        prepStmt.setString(1, GovernanceUtil.generateUUID());
                        prepStmt.setString(2, governancePolicy.getId());
                        prepStmt.setString(3, action.getState());
                        prepStmt.setString(4, action.getRuleSeverity());
                        prepStmt.setString(5, action.getType());
                        prepStmt.addBatch();
                    }
                    prepStmt.executeBatch();
                }
                connection.commit();
                return getGovernancePolicyByID(organization, governancePolicy.getId()); // to return saved policy
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                if (getGovernancePolicyByName(organization, governancePolicy.getName()) != null) {
                    throw new GovernanceException(GovernanceExceptionCodes.POLICY_ALREADY_EXISTS, e,
                            governancePolicy.getName(), organization);
                }
            }
            throw new GovernanceException(GovernanceExceptionCodes.ERROR_WHILE_CREATING_POLICY, e, organization);
        }
    }


    /**
     * Get Governance Policy by Name
     *
     * @param organization Organization
     * @param policyName   Policy Name
     * @return GovernancePolicy
     * @throws GovernanceException If an error occurs while retrieving the policy
     */
    @Override
    public GovernancePolicy getGovernancePolicyByName(String organization, String policyName) throws GovernanceException {
        GovernancePolicy policy = null;
        try (Connection connection = GovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.GET_POLICY_BY_NAME)) {
            prepStmt.setString(1, organization);
            prepStmt.setString(2, policyName);
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                if (resultSet.next()) {
                    policy = new GovernancePolicy();
                    policy.setId(resultSet.getString("POLICY_ID"));
                    policy.setName(resultSet.getString("NAME"));
                    policy.setDescription(resultSet.getString("DESCRIPTION"));
                    policy.setCreatedBy(resultSet.getString("CREATED_BY"));
                    policy.setCreatedTime(resultSet.getString("CREATED_TIME"));
                    policy.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    policy.setUpdatedTime(resultSet.getString("LAST_UPDATED_TIME"));
                }
            }
            return policy;
        } catch (SQLException e) {
            throw new GovernanceException(GovernanceExceptionCodes.ERROR_WHILE_RETRIEVING_POLICY_BY_NAME, e, policyName,
                    organization);
        }
    }

    /**
     * Get Governance Policy by ID
     *
     * @param organization Organization
     * @param policyID     Policy ID
     * @return GovernancePolicy
     * @throws GovernanceException If an error occurs while retrieving the policy
     */
    @Override
    public GovernancePolicy getGovernancePolicyByID(String organization, String policyID) throws GovernanceException {
        GovernancePolicy policy = null;
        try (Connection connection = GovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.GET_POLICY_BY_ID)) {
            prepStmt.setString(1, organization);
            prepStmt.setString(2, policyID);
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                if (resultSet.next()) {
                    policy = new GovernancePolicy();
                    policy.setId(resultSet.getString("POLICY_ID"));
                    policy.setName(resultSet.getString("NAME"));
                    policy.setDescription(resultSet.getString("DESCRIPTION"));
                    policy.setCreatedBy(resultSet.getString("CREATED_BY"));
                    policy.setCreatedTime(resultSet.getString("CREATED_TIME"));
                    policy.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    policy.setUpdatedTime(resultSet.getString("LAST_UPDATED_TIME"));
                    policy.setRulesetIds(getRulesetsByPolicyId(policy.getId(), connection));
                    policy.setLabels(getLabelsByPolicyId(policy.getId(), connection));
                    policy.setApplicableStates(getStatesByPolicyId(policy.getId(), connection));
                    policy.setActions(getActionsByPolicyId(policy.getId(), connection));
                }
            }
            return policy;
        } catch (SQLException e) {
            throw new GovernanceException(GovernanceExceptionCodes.ERROR_WHILE_RETRIEVING_POLICY_BY_ID, e, policyID,
                    organization);
        }
    }

    /**
     * Get all the Governance Policies
     *
     * @param organization Organization
     * @return GovernancePolicyList object
     * @throws GovernanceException If an error occurs while getting the policies
     */
    @Override
    public GovernancePolicyList getGovernancePolicies(String organization) throws GovernanceException {
        GovernancePolicyList policyListObj = new GovernancePolicyList();
        List<GovernancePolicy> policyList = new ArrayList<>();
        try (Connection connection = GovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.GET_POLICIES)) {
            prepStmt.setString(1, organization);
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    GovernancePolicy policy = new GovernancePolicy();
                    policy.setId(resultSet.getString("POLICY_ID"));
                    policy.setName(resultSet.getString("NAME"));
                    policy.setDescription(resultSet.getString("DESCRIPTION"));
                    policy.setCreatedBy(resultSet.getString("CREATED_BY"));
                    policy.setCreatedTime(resultSet.getString("CREATED_TIME"));
                    policy.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    policy.setUpdatedTime(resultSet.getString("LAST_UPDATED_TIME"));
                    policy.setRulesetIds(getRulesetsByPolicyId(policy.getId(), connection));
                    policy.setLabels(getLabelsByPolicyId(policy.getId(), connection));
                    policy.setActions(getActionsByPolicyId(policy.getId(), connection));
                    policy.setApplicableStates(getStatesByPolicyId(policy.getId(), connection));
                    policyList.add(policy);
                }
            }
            policyListObj.setCount(policyList.size());
            policyListObj.setGovernancePolicyList(policyList);
            return policyListObj;
        } catch (SQLException e) {
            throw new GovernanceException(GovernanceExceptionCodes.ERROR_WHILE_RETRIEVING_POLICIES,
                    e, organization);
        }
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
        try (Connection connection = GovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(SQLConstants
                     .DELETE_GOVERNANCE_POLICY)) {
            connection.setAutoCommit(false);
            try {
                prepStmt.setString(1, policyId);
                prepStmt.setString(2, organization);
                int rowsAffected = prepStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new GovernanceException(GovernanceExceptionCodes.POLICY_NOT_FOUND,
                            policyId, organization);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new GovernanceException(GovernanceExceptionCodes.ERROR_WHILE_DELETING_POLICY,
                    e, policyId, organization);
        }
    }

    /**
     * Update a Governance Policy
     *
     * @param policyId         Policy ID
     * @param organization     Organization
     * @param governancePolicy Governance Policy Info with Ruleset Ids
     * @return GovernancePolicy Updated object
     * @throws GovernanceException If an error occurs while updating the policy
     */
    @Override
    public GovernancePolicy updateGovernancePolicy(String policyId, String organization,
                                                   GovernancePolicy governancePolicy) throws GovernanceException {
        Timestamp timestamp;
        try (Connection connection = GovernanceDBUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Update policy details
                try (PreparedStatement updateStatement = connection.prepareStatement(SQLConstants.UPDATE_POLICY)) {
                    updateStatement.setString(1, governancePolicy.getName());
                    updateStatement.setString(2, governancePolicy.getDescription());
                    updateStatement.setString(3, governancePolicy.getUpdatedBy());

                    timestamp = new Timestamp(System.currentTimeMillis());
                    governancePolicy.setUpdatedTime(timestamp.toString());
                    updateStatement.setTimestamp(4, timestamp);

                    updateStatement.setString(5, policyId);
                    updateStatement.setString(6, organization);
                    updateStatement.executeUpdate();
                }
                // Retrieve existing rulesets
                List<String> existingRulesets = new ArrayList<>();
                try (PreparedStatement getRulesetsStatement =
                             connection.prepareStatement(SQLConstants.GET_RULESET_IDS_BY_POLICY_ID)) {
                    getRulesetsStatement.setString(1, policyId);
                    try (ResultSet resultSet = getRulesetsStatement.executeQuery()) {
                        while (resultSet.next()) {
                            existingRulesets.add(resultSet.getString("RULESET_ID"));
                        }
                    }
                }
                // Determine rulesets to add and remove
                List<String> rulesetsToBeUpdatedList = governancePolicy.getRulesetIds();
                List<String> rulesetsToAdd = new ArrayList<>(rulesetsToBeUpdatedList);
                List<String> rulesetsToRemove = new ArrayList<>(existingRulesets);
                rulesetsToAdd.removeAll(existingRulesets);
                rulesetsToRemove.removeAll(rulesetsToBeUpdatedList);
                // Add new rulesets
                if (!rulesetsToAdd.isEmpty()) {
                    try (PreparedStatement insertStatement =
                                 connection.prepareStatement(SQLConstants.CREATE_POLICY_RULESET_MAPPING)) {
                        for (String rulesetId : rulesetsToAdd) {
                            insertStatement.setString(1, GovernanceUtil.generateUUID());
                            insertStatement.setString(2, policyId);
                            insertStatement.setString(3, rulesetId);
                            insertStatement.addBatch();
                        }
                        insertStatement.executeBatch();
                    }
                }
                // Remove old rulesets
                if (!rulesetsToRemove.isEmpty()) {
                    try (PreparedStatement deleteStatement =
                                 connection.prepareStatement(SQLConstants.DELETE_POLICY_RULESET_MAPPING)) {
                        for (String rulesetId : rulesetsToRemove) {
                            deleteStatement.setString(1, policyId);
                            deleteStatement.setString(2, rulesetId);
                            deleteStatement.addBatch();
                        }
                        deleteStatement.executeBatch();
                    }
                }

                updateLabelsForPolicy(policyId, governancePolicy, connection);
                updateApplicableStatesForPolicy(policyId, governancePolicy, connection);
                updateActionsForPolicy(policyId, governancePolicy, connection);

                // return updated GovernancePolicy object
                connection.commit();
                return getGovernancePolicyByID(organization, policyId);
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new GovernanceException(GovernanceExceptionCodes.ERROR_WHILE_UPDATING_POLICY, e,
                    policyId, organization);
        }
    }

    /**
     * Update applicable states for a Policy
     *
     * @param policyId         Policy ID
     * @param governancePolicy Governance Policy Info with Ruleset Ids
     * @param connection       DB Connection
     * @throws SQLException If an error occurs while updating the states (Captured at higher level)
     */
    private void updateApplicableStatesForPolicy(String policyId, GovernancePolicy governancePolicy,
                                                 Connection connection) throws SQLException {
        // Retrieve applicable States
        List<String> existingStates = new ArrayList<>();
        try (PreparedStatement getStatesStatement =
                     connection.prepareStatement(SQLConstants.GET_STATES_BY_POLICY_ID)) {
            getStatesStatement.setString(1, policyId);
            try (ResultSet resultSet = getStatesStatement.executeQuery()) {
                while (resultSet.next()) {
                    existingStates.add(resultSet.getString("STATE"));
                }
            }
        }
        // Determine states to add and remove
        List<String> statesToBeUpdated = governancePolicy.getApplicableStates();
        List<String> statesToAdd = new ArrayList<>(statesToBeUpdated);
        List<String> statesToRemove = new ArrayList<>(existingStates);
        statesToAdd.removeAll(existingStates);
        statesToRemove.removeAll(statesToBeUpdated);
        // Add new states
        if (!statesToAdd.isEmpty()) {
            try (PreparedStatement insertStatement =
                         connection.prepareStatement(SQLConstants.CREATE_GOVERNANCE_POLICY_STATE_MAPPING)) {
                for (String state : statesToAdd) {
                    insertStatement.setString(1, GovernanceUtil.generateUUID());
                    insertStatement.setString(2, policyId);
                    insertStatement.setString(3, state);
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        }
        // Remove old states
        if (!statesToRemove.isEmpty()) {
            try (PreparedStatement deleteStatement =
                         connection.prepareStatement(SQLConstants.DELETE_GOVERNANCE_POLICY_STATE_MAPPING)) {
                for (String state : statesToRemove) {
                    deleteStatement.setString(1, policyId);
                    deleteStatement.setString(2, state);
                    deleteStatement.addBatch();
                }
                deleteStatement.executeBatch();
            }
        }
    }

    /**
     * Update labels for a Policy
     *
     * @param policyId         Policy ID
     * @param governancePolicy Governance Policy Info with Ruleset Ids
     * @param connection       DB Connection
     * @throws SQLException If an error occurs while updating the labels (Captured at higher level)
     */
    private void updateLabelsForPolicy(String policyId, GovernancePolicy governancePolicy,
                                       Connection connection) throws SQLException {
        // Retrieve existing labels
        List<String> existingLabels = new ArrayList<>();
        try (PreparedStatement getLabelsStatement =
                     connection.prepareStatement(SQLConstants.GET_LABELS_BY_POLICY_ID)) {
            getLabelsStatement.setString(1, policyId);
            try (ResultSet resultSet = getLabelsStatement.executeQuery()) {
                while (resultSet.next()) {
                    existingLabels.add(resultSet.getString("LABEL"));
                }
            }
        }
        // Determine labels to add and remove
        List<String> labelsToBeUpdated = governancePolicy.getLabels();
        List<String> labelsToAdd = new ArrayList<>(labelsToBeUpdated);
        List<String> labelsToRemove = new ArrayList<>(existingLabels);
        labelsToAdd.removeAll(existingLabels);
        labelsToRemove.removeAll(labelsToBeUpdated);
        // Add new labels
        if (!labelsToAdd.isEmpty()) {
            try (PreparedStatement insertStatement =
                         connection.prepareStatement(SQLConstants.CREATE_GOVERNANCE_POLICY_LABEL_MAPPING)) {
                for (String label : labelsToAdd) {
                    insertStatement.setString(1, GovernanceUtil.generateUUID());
                    insertStatement.setString(2, policyId);
                    insertStatement.setString(3, label);
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        }
        // Remove old labels
        if (!labelsToRemove.isEmpty()) {
            try (PreparedStatement deleteStatement =
                         connection.prepareStatement(SQLConstants.DELETE_GOVERNANCE_POLICY_LABEL_MAPPING)) {
                for (String label : labelsToRemove) {
                    deleteStatement.setString(1, policyId);
                    deleteStatement.setString(2, label);
                    deleteStatement.addBatch();
                }
                deleteStatement.executeBatch();
            }
        }
    }

    /**
     * Update actions for a Policy
     *
     * @param policyId         Policy ID
     * @param governancePolicy Governance Policy Info with Ruleset Ids
     * @param connection       DB Connection
     * @throws SQLException If an error occurs while updating the actions (Captured at higher level)
     */
    private void updateActionsForPolicy(String policyId, GovernancePolicy governancePolicy,
                                        Connection connection) throws SQLException {
        // Retrieve existing actions
        List<GovernanceAction> existingActions = new ArrayList<>();
        try (PreparedStatement getActionsStatement =
                     connection.prepareStatement(SQLConstants.GET_ACTIONS_BY_POLICY_ID)) {
            getActionsStatement.setString(1, policyId);
            try (ResultSet resultSet = getActionsStatement.executeQuery()) {
                while (resultSet.next()) {
                    GovernanceAction action = new GovernanceAction();
                    action.setState(resultSet.getString("STATE"));
                    action.setRuleSeverity(resultSet.getString("SEVERITY"));
                    action.setType(resultSet.getString("TYPE"));
                    existingActions.add(action);
                }
            }
        }
        // Determine actions to add and remove
        List<GovernanceAction> actionsToBeUpdated = governancePolicy.getActions();
        List<GovernanceAction> actionsToAdd = new ArrayList<>(actionsToBeUpdated);
        List<GovernanceAction> actionsToRemove = new ArrayList<>(existingActions);
        actionsToAdd.removeAll(existingActions);
        actionsToRemove.removeAll(actionsToBeUpdated);
        // Add new actions
        if (!actionsToAdd.isEmpty()) {
            try (PreparedStatement insertStatement =
                         connection.prepareStatement(SQLConstants.CREATE_GOVERNANCE_POLICY_ACTION_MAPPING)) {
                for (GovernanceAction action : actionsToAdd) {
                    insertStatement.setString(1, GovernanceUtil.generateUUID());
                    insertStatement.setString(2, policyId);
                    insertStatement.setString(3, action.getState());
                    insertStatement.setString(4, action.getRuleSeverity());
                    insertStatement.setString(5, action.getType());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
        }
        // Remove old actions
        if (!actionsToRemove.isEmpty()) {
            try (PreparedStatement deleteStatement =
                         connection.prepareStatement(SQLConstants.DELETE_GOVERNANCE_POLICY_ACTION_MAPPING)) {
                for (GovernanceAction action : actionsToRemove) {
                    deleteStatement.setString(1, policyId);
                    deleteStatement.setString(2, action.getState());
                    deleteStatement.setString(3, action.getRuleSeverity());
                    deleteStatement.setString(4, action.getType());
                    deleteStatement.addBatch();
                }
                deleteStatement.executeBatch();
            }
        }
    }

    /**
     * Get all the Rulesets attached to a Policy
     *
     * @param policyId   Policy ID
     * @param connection DB Connection
     * @return List of Rulesets
     * @throws SQLException If an error occurs while retrieving the rulesets (Captured at higher level)
     */
    private List<String> getRulesetsByPolicyId(String policyId, Connection connection) throws SQLException {
        List<String> rulesetIds = new ArrayList<>();
        String sqlQuery = SQLConstants.GET_RULESET_IDS_BY_POLICY_ID;
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
            prepStmt.setString(1, policyId);
            try (ResultSet rs = prepStmt.executeQuery()) {
                while (rs.next()) {
                    rulesetIds.add(rs.getString("RULESET_ID"));
                }
            }
        }
        return rulesetIds;
    }

    /**
     * Get all the Labels attached to a Policy
     *
     * @param policyId   Policy ID
     * @param connection DB Connection
     * @return List of Labels
     * @throws SQLException If an error occurs while retrieving the labels (Captured at higher level)
     */
    private List<String> getLabelsByPolicyId(String policyId, Connection connection) throws SQLException {
        List<String> labels = new ArrayList<>();
        try (PreparedStatement prepStmt =
                     connection.prepareStatement(SQLConstants.GET_LABELS_BY_POLICY_ID)) {
            prepStmt.setString(1, policyId);
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    labels.add(resultSet.getString("LABEL"));
                }
            }
        }
        return labels;
    }

    /**
     * Get all the States attached to a Policy
     *
     * @param policyId   Policy ID
     * @param connection DB Connection
     * @return List of States
     * @throws SQLException If an error occurs while retrieving the states (Captured at higher level)
     */
    private List<String> getStatesByPolicyId(String policyId, Connection connection) throws SQLException {
        List<String> states = new ArrayList<>();
        try (PreparedStatement prepStmt =
                     connection.prepareStatement(SQLConstants.GET_STATES_BY_POLICY_ID)) {
            prepStmt.setString(1, policyId);
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    states.add(resultSet.getString("STATE"));
                }
            }
        }
        return states;
    }


    /**
     * Get all the Actions attached to a Policy
     *
     * @param policyId   Policy ID
     * @param connection DB Connection
     * @return List of Actions
     * @throws SQLException If an error occurs while retrieving the actions (Captured at higher level)
     */
    private List<GovernanceAction> getActionsByPolicyId(String policyId, Connection connection) throws SQLException {
        List<GovernanceAction> actions = new ArrayList<>();
        try (PreparedStatement prepStmt =
                     connection.prepareStatement(SQLConstants.GET_ACTIONS_BY_POLICY_ID)) {
            prepStmt.setString(1, policyId);
            try (ResultSet resultSet = prepStmt.executeQuery()) {
                while (resultSet.next()) {
                    GovernanceAction action = new GovernanceAction();
                    action.setState(resultSet.getString("STATE"));
                    action.setRuleSeverity(resultSet.getString("SEVERITY"));
                    action.setType(resultSet.getString("TYPE"));
                    actions.add(action);
                }
            }
        }
        return actions;
    }


}



