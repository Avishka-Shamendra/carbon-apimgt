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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.governance.api.error.APIMGovExceptionCodes;
import org.wso2.carbon.apimgt.governance.api.error.APIMGovernanceException;
import org.wso2.carbon.apimgt.governance.api.model.ExtendedArtifactType;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicy;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyCategory;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyContent;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyInfo;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyList;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyType;
import org.wso2.carbon.apimgt.governance.api.model.Rule;
import org.wso2.carbon.apimgt.governance.api.model.RuleSeverity;
import org.wso2.carbon.apimgt.governance.impl.APIMGovernanceConstants;
import org.wso2.carbon.apimgt.governance.impl.dao.PolicyMgtDAO;
import org.wso2.carbon.apimgt.governance.impl.dao.constants.SQLConstants;
import org.wso2.carbon.apimgt.governance.impl.util.APIMGovernanceDBUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the PolicyMgtDAO interface.
 */
public class PolicyMgtDAOImpl implements PolicyMgtDAO {

    private static final Log log = LogFactory.getLog(PolicyMgtDAOImpl.class);

    private PolicyMgtDAOImpl() {
    }

    private static class SingletonHelper {
        private static final PolicyMgtDAO INSTANCE = new PolicyMgtDAOImpl();
    }

    /**
     * Get the instance of the PolicyMgtDAOImpl
     *
     * @return PolicyMgtDAOImpl instance
     */
    public static PolicyMgtDAO getInstance() {

        return SingletonHelper.INSTANCE;
    }

    /**
     * Create a new Governance Policy
     *
     * @param policy       Policy object
     * @param rules        List of rules
     * @param organization Organization
     * @return PolicyInfo Created object
     * @throws APIMGovernanceException If an error occurs while creating the policy
     */
    @Override
    public APIMGovPolicyInfo createPolicy(APIMGovPolicy policy, List<Rule> rules,
                                          String organization) throws APIMGovernanceException {

        String sqlQuery = SQLConstants.CREATE_POLICY;
        try (Connection connection = APIMGovernanceDBUtil.getConnection()) {
            try {
                connection.setAutoCommit(false);
                try (PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
                    prepStmt.setString(1, policy.getId());
                    prepStmt.setString(2, policy.getName());
                    prepStmt.setString(3, policy.getDescription());
                    prepStmt.setString(4, String.valueOf(policy.getPolicyCategory()));
                    prepStmt.setString(5, String.valueOf(policy.getPolicyType()));
                    prepStmt.setString(6, String.valueOf(policy.getArtifactType()));
                    prepStmt.setString(7, policy.getDocumentationLink());
                    prepStmt.setString(8, policy.getProvider());
                    prepStmt.setString(9, organization);
                    prepStmt.setString(10, policy.getCreatedBy());

                    Timestamp createdTime = new Timestamp(System.currentTimeMillis());
                    prepStmt.setTimestamp(11, createdTime);
                    policy.setCreatedTime(createdTime.toString());

                    prepStmt.execute();
                }
                addPolicyContent(connection, policy.getId(), policy.getPolicyContent());
                addRules(policy.getId(), rules, connection);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException | IOException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.POLICY_CREATION_FAILED, e,
                    policy.getName(), organization
            );
        }
        return getPolicyInfoByPolicy(policy);
    }

    /**
     * Update a Governance Policy
     *
     * @param policyId     Policy ID
     * @param policy       Policy object
     * @param rules        List of rules
     * @param organization Organization
     * @return PolicyInfo Created object
     * @throws APIMGovernanceException If an error occurs while updating the policy
     */
    @Override
    public APIMGovPolicyInfo updatePolicy(String policyId, APIMGovPolicy policy, List<Rule> rules,
                                          String organization)
            throws APIMGovernanceException {

        try (Connection connection = APIMGovernanceDBUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.UPDATE_POLICY)) {
                    prepStmt.setString(1, policy.getName());
                    prepStmt.setString(2, policy.getDescription());
                    prepStmt.setString(3, String.valueOf(policy.getPolicyCategory()));
                    prepStmt.setString(4, String.valueOf(policy.getPolicyType()));
                    prepStmt.setString(5, String.valueOf(policy.getArtifactType()));
                    prepStmt.setString(6, policy.getDocumentationLink());
                    prepStmt.setString(7, policy.getProvider());
                    prepStmt.setString(8, policy.getUpdatedBy());

                    Timestamp updatedTime = new Timestamp(System.currentTimeMillis());
                    prepStmt.setTimestamp(9, updatedTime);
                    policy.setUpdatedTime(updatedTime.toString());

                    prepStmt.setString(10, policyId);
                    prepStmt.setString(11, organization);
                    prepStmt.executeUpdate();
                }
                updatePolicyContent(connection, policy.getId(), policy.getPolicyContent());

                // Delete existing rules and rule evaluation results related to this policy
                deleteRuleViolationsForPolicy(connection, policyId);
                deletePolicyRunsForPolicy(connection, policyId);
                deleteRules(connection, policyId);

                // Insert updated rules to the database
                addRules(policy.getId(), rules, connection);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException | IOException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_UPDATING_POLICY, e, policyId);
        }
        return getPolicyInfoByPolicy(policy);
    }

    /**
     * Get the Policy object from the PolicyInfo object
     *
     * @param policy PolicyInfo object
     * @return Policy object
     */
    private APIMGovPolicyInfo getPolicyInfoByPolicy(APIMGovPolicy policy) {
        APIMGovPolicyInfo policyInfo = new APIMGovPolicyInfo();
        policyInfo.setId(policy.getId());
        policyInfo.setName(policy.getName());
        policyInfo.setDescription(policy.getDescription());
        policyInfo.setPolicyCategory(policy.getPolicyCategory());
        policyInfo.setPolicyType(policy.getPolicyType());
        policyInfo.setArtifactType(policy.getArtifactType());
        policyInfo.setDocumentationLink(policy.getDocumentationLink());
        policyInfo.setProvider(policy.getProvider());
        policyInfo.setCreatedBy(policy.getCreatedBy());
        policyInfo.setCreatedTime(policy.getCreatedTime());
        policyInfo.setUpdatedBy(policy.getUpdatedBy());
        policyInfo.setUpdatedTime(policy.getUpdatedTime());
        return policyInfo;
    }

    /**
     * Add rules in a policy to DB
     *
     * @param policyId   Policy ID
     * @param rules      List of rules
     * @param connection Database connection
     * @throws SQLException If an error occurs while adding the rules
     */
    private void addRules(String policyId, List<Rule> rules, Connection connection)
            throws SQLException {
        String sqlQuery = SQLConstants.ADD_RULES;
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlQuery);) {
            for (Rule rule : rules) {
                prepStmt.setString(1, rule.getId());
                prepStmt.setString(2, policyId);
                prepStmt.setString(3, rule.getName());
                prepStmt.setString(4, rule.getDescription());
                prepStmt.setString(5, String.valueOf(rule.getSeverity()));
                prepStmt.setBlob(6, new ByteArrayInputStream(rule.getContent()
                        .getBytes(Charset.defaultCharset())));
                prepStmt.addBatch();
            }
            prepStmt.executeBatch();
        }

    }

    /**
     * Add the content of a Governance Policy
     *
     * @param connection    Database connection
     * @param policyId      Policy ID
     * @param policyContent Policy content
     * @throws SQLException If an error occurs while adding the policy content
     * @throws IOException  If an error occurs while adding the policy content
     */
    private void addPolicyContent(Connection connection, String policyId, APIMGovPolicyContent policyContent)
            throws SQLException, IOException {
        String sqlQuery = SQLConstants.ADD_POLICY_CONTENT;
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlQuery);
             InputStream contentStream = new ByteArrayInputStream(policyContent.getContent())) {
            prepStmt.setString(1, policyId);
            prepStmt.setBlob(2, contentStream);
            prepStmt.setString(3, policyContent.getContentType().toString());
            prepStmt.setString(4, policyContent.getFileName());
            prepStmt.execute();
        }
    }

    /**
     * Update the content of a Governance Policy
     *
     * @param connection    Database connection
     * @param policyId      Policy ID
     * @param policyContent Policy content
     * @throws SQLException If an error occurs while updating the policy content
     * @throws IOException  If an error occurs while updating the policy content
     */
    private void updatePolicyContent(Connection connection, String policyId, APIMGovPolicyContent policyContent)
            throws SQLException, IOException {
        String sqlQuery = SQLConstants.UPDATE_POLICY_CONTENT;
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlQuery);
             InputStream contentStream = new ByteArrayInputStream(policyContent.getContent())) {
            prepStmt.setBlob(1, contentStream);
            prepStmt.setString(2, policyContent.getContentType().toString());
            prepStmt.setString(3, policyContent.getFileName());
            prepStmt.setString(4, policyId);
            prepStmt.execute();
        }
    }

    /**
     * Delete a Governance Policy
     *
     * @param policyId     Policy ID
     * @param organization Organization
     * @throws APIMGovernanceException If an error occurs while deleting the policy
     */
    @Override
    public void deletePolicy(String policyId, String organization) throws APIMGovernanceException {

        try (Connection connection = APIMGovernanceDBUtil.getConnection()) {

            connection.setAutoCommit(false);
            try {
                deleteRuleViolationsForPolicy(connection, policyId);
                deletePolicyRunsForPolicy(connection, policyId);
                deletePolicyContent(connection, policyId);
                deleteRules(connection, policyId);

                try (PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.DELETE_POLICY)) {
                    prepStmt.setString(1, policyId);
                    prepStmt.setString(2, organization);
                    prepStmt.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_DELETING_POLICY,
                    e, policyId);
        }
    }

    /**
     * Delete the content of a Governance Policy
     *
     * @param connection Database connection
     * @param policyId   Policy ID
     * @throws SQLException If an error occurs while deleting the policy content
     */
    private void deletePolicyContent(Connection connection, String policyId) throws SQLException {
        try (PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.DELETE_POLICY_CONTENT)) {
            prepStmt.setString(1, policyId);
            prepStmt.executeUpdate();
        }
    }

    /**
     * Delete rules related to a policy
     *
     * @param connection Database connection
     * @param policyId   Policy ID
     * @throws SQLException If an error occurs while deleting the rules
     */
    private void deleteRules(Connection connection, String policyId) throws SQLException {
        try (PreparedStatement prepStmt = connection.prepareStatement(SQLConstants.DELETE_RULES)) {
            prepStmt.setString(1, policyId);
            prepStmt.executeUpdate();
        }
    }

    /**
     * Delete rule violations related to a policy
     *
     * @param connection Database connection
     * @param policyId   Policy ID
     * @throws SQLException If an error occurs while checking the association
     */
    private void deleteRuleViolationsForPolicy(Connection connection, String policyId)
            throws SQLException {
        try (PreparedStatement prepStmt = connection.prepareStatement(SQLConstants
                .DELETE_RULE_VIOLATIONS_FOR_POLICY)) {
            prepStmt.setString(1, policyId);
            prepStmt.executeUpdate();
        }
    }

    /**
     * Delete policy runs related to a policy
     *
     * @param connection Database connection
     * @param policyId   Policy ID
     * @throws SQLException If an error occurs while deleting
     */
    private void deletePolicyRunsForPolicy(Connection connection, String policyId)
            throws SQLException {
        try (PreparedStatement prepStmt = connection.
                prepareStatement(SQLConstants.DELETE_POLICY_RUN_FOR_POLICY)) {
            prepStmt.setString(1, policyId);
            prepStmt.executeUpdate();
        }
    }

    /**
     * Retrieves policies in the organization.
     *
     * @param organization Organization whose policies are to be retrieved
     * @return a list of policies associated with the organization
     * @throws APIMGovernanceException if there is an error retrieving the policies
     */
    @Override
    public APIMGovPolicyList getPolicies(String organization) throws APIMGovernanceException {
        APIMGovPolicyList policyList = new APIMGovPolicyList();
        List<APIMGovPolicyInfo> policyInfoList = new ArrayList<>();
        String sqlQuery = SQLConstants.GET_POLICIES;
        try (Connection connection = APIMGovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
            prepStmt.setString(1, organization);
            try (ResultSet rs = prepStmt.executeQuery()) {
                while (rs.next()) {
                    policyInfoList.add(getPolicyInfoFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_RETRIEVING_POLICIES,
                    e, organization);
        }
        policyList.setCount(policyInfoList.size());
        policyList.setPolicyList(policyInfoList);
        return policyList;
    }

    /**
     * Retrieves a policy by name.
     *
     * @param name         Name of the policy
     * @param organization Organization whose policy is to be retrieved
     * @return the policy with the given name
     * @throws APIMGovernanceException if there is an error retrieving the policy
     */
    @Override
    public APIMGovPolicyInfo getPolicyByName(String name, String organization) throws APIMGovernanceException {
        String sqlQuery = SQLConstants.GET_POLICY_BY_NAME;
        try (Connection connection = APIMGovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
            prepStmt.setString(1, name);
            prepStmt.setString(2, organization);
            try (ResultSet rs = prepStmt.executeQuery()) {
                if (rs.next()) {
                    return getPolicyInfoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_RETRIEVING_POLICY_BY_NAME,
                    e, organization);
        }
        return null;
    }

    /**
     * Retrieves a policy by ID.
     *
     * @param policyId     Policy ID of the policy
     * @param organization Organization whose policy is to be retrieved
     * @return the policy with the given ID
     * @throws APIMGovernanceException if there is an error retrieving the policy
     */
    @Override
    public APIMGovPolicyInfo getPolicyById(String policyId, String organization) throws APIMGovernanceException {
        String sqlQuery = SQLConstants.GET_POLICIES_BY_ID;
        try (Connection connection = APIMGovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
            prepStmt.setString(1, policyId);
            prepStmt.setString(2, organization);
            try (ResultSet rs = prepStmt.executeQuery()) {
                if (rs.next()) {
                    return getPolicyInfoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_RETRIEVING_POLICY_BY_ID,
                    e);
        }
        return null;
    }

    /**
     * Search for Governance Policies based on the search criteria
     *
     * @param searchCriteria Search attributes
     * @param organization   Organization
     * @return PolicyList object
     * @throws APIMGovernanceException If an error occurs while searching for policies
     */
    @Override
    public APIMGovPolicyList searchPolicies(Map<String, String> searchCriteria, String organization)
            throws APIMGovernanceException {
        APIMGovPolicyList policyList = new APIMGovPolicyList();
        List<APIMGovPolicyInfo> policyInfoList = new ArrayList<>();

        String sqlQuery = SQLConstants.SEARCH_POLICIES;
        try (Connection connection = APIMGovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
            prepStmt.setString(1, organization);
            prepStmt.setString(2, searchCriteria
                    .getOrDefault(APIMGovernanceConstants.PolicySearchAttributes.NAME, ""));
            prepStmt.setString(3, searchCriteria
                    .getOrDefault(APIMGovernanceConstants.PolicySearchAttributes.POLICY_TYPE, ""));
            prepStmt.setString(4, searchCriteria
                    .getOrDefault(APIMGovernanceConstants.PolicySearchAttributes.ARTIFACT_TYPE, ""));
            try (ResultSet rs = prepStmt.executeQuery()) {
                while (rs.next()) {
                    policyInfoList.add(getPolicyInfoFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_SEARCHING_POLICIES,
                    e, organization);
        }
        policyList.setCount(policyInfoList.size());
        policyList.setPolicyList(policyInfoList);
        return policyList;
    }

    /**
     * Retrieves PolicyInfo object from the result set
     *
     * @param rs ResultSet
     * @return PolicyInfo object
     * @throws SQLException If an error occurs while retrieving the policy
     */
    private APIMGovPolicyInfo getPolicyInfoFromResultSet(ResultSet rs) throws SQLException {
        APIMGovPolicyInfo policyInfo = new APIMGovPolicyInfo();
        policyInfo.setId(rs.getString("POLICY_ID"));
        policyInfo.setName(rs.getString("NAME"));
        policyInfo.setDescription(rs.getString("DESCRIPTION"));
        policyInfo.setPolicyCategory(APIMGovPolicyCategory.fromString(
                rs.getString("POLICY_CATEGORY")));
        policyInfo.setPolicyType(APIMGovPolicyType.fromString(rs.getString("POLICY_TYPE")));
        policyInfo.setArtifactType(ExtendedArtifactType.fromString(
                rs.getString("ARTIFACT_TYPE")));
        policyInfo.setDocumentationLink(rs.getString("DOCUMENTATION_LINK"));
        policyInfo.setProvider(rs.getString("PROVIDER"));
        policyInfo.setCreatedBy(rs.getString("CREATED_BY"));
        policyInfo.setCreatedTime(rs.getString("CREATED_TIME"));
        policyInfo.setUpdatedBy(rs.getString("UPDATED_BY"));
        policyInfo.setUpdatedTime(rs.getString("LAST_UPDATED_TIME"));
        return policyInfo;
    }

    /**
     * Get the content of a Governance Policy
     *
     * @param policyId     Policy ID
     * @param organization Organization
     * @return String Content of the policy
     * @throws APIMGovernanceException If an error occurs while getting the policy content
     */
    @Override
    public APIMGovPolicyContent getPolicyContent(String policyId, String organization) throws APIMGovernanceException {
        String sqlQuery = SQLConstants.GET_POLICY_CONTENT;
        try (Connection connection = APIMGovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sqlQuery);) {
            prepStmt.setString(1, policyId);
            prepStmt.setString(2, organization);
            try (ResultSet rs = prepStmt.executeQuery()) {
                if (rs.next()) {
                    APIMGovPolicyContent policyContentObj = new APIMGovPolicyContent();
                    policyContentObj.setFileName(rs.getString("FILE_NAME"));
                    policyContentObj.setContent(rs.getBytes("CONTENT"));
                    return policyContentObj;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_RETRIEVING_POLICY_BY_ID,
                    e);
        }
    }

    /**
     * Get the associated policy attachments for a policy
     *
     * @param policyId     Policy ID
     * @param organization Organization
     * @return List of associated policy attachments
     */
    @Override
    public List<String> getAssociatedPolicyAttachmentForPolicy(String policyId, String organization)
            throws APIMGovernanceException {
        List<String> policyAttachmentIds = new ArrayList<>();
        String sqlQuery = SQLConstants.GET_POLICY_ATTACHMENT_FOR_POLICY;
        try (Connection connection = APIMGovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
            prepStmt.setString(1, policyId);
            prepStmt.setString(2, organization);
            try (ResultSet rs = prepStmt.executeQuery()) {
                while (rs.next()) {
                    policyAttachmentIds.add(rs.getString("POLICY_ATTACHMENT_ID"));
                }
            }
        } catch (SQLException e) {
            throw new APIMGovernanceException(
                    APIMGovExceptionCodes.ERROR_WHILE_RETRIEVING_ASSOCIATED_POLICY_ATTACHMENTS, e, policyId);
        }
        return policyAttachmentIds;
    }

    /**
     * Get the rules of a Policy (without the content)
     *
     * @param policyId     Policy ID
     * @param organization Organization
     * @return List of rules
     */
    @Override
    public List<Rule> getPolicyByPolicyId(String policyId, String organization) throws APIMGovernanceException {
        List<Rule> rules = new ArrayList<>();
        String sqlQuery = SQLConstants.GET_RULES_WITHOUT_CONTENT;
        try (Connection connection = APIMGovernanceDBUtil.getConnection();
             PreparedStatement prepStmt = connection.prepareStatement(sqlQuery)) {
            prepStmt.setString(1, policyId);
            prepStmt.setString(2, organization);
            try (ResultSet rs = prepStmt.executeQuery()) {
                while (rs.next()) {
                    Rule rule = new Rule();
                    rule.setId(rs.getString("POLICY_RULE_ID"));
                    rule.setName(rs.getString("RULE_NAME"));
                    rule.setDescription(rs.getString("RULE_DESCRIPTION"));
                    rule.setSeverity(RuleSeverity.fromString(rs.getString("SEVERITY")));
                    rules.add(rule);
                }
            }
            return rules;
        } catch (SQLException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_WHILE_RETRIEVING_RULES_BY_POLICY_ID
                    , e, policyId);
        }
    }
}

