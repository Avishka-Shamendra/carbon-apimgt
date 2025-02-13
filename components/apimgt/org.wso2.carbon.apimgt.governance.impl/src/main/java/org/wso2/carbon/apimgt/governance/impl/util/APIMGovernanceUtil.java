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

package org.wso2.carbon.apimgt.governance.impl.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.governance.api.error.APIMGovExceptionCodes;
import org.wso2.carbon.apimgt.governance.api.error.APIMGovernanceException;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovernableState;
import org.wso2.carbon.apimgt.governance.api.model.ArtifactType;
import org.wso2.carbon.apimgt.governance.api.model.DefaultGovPolicy;
import org.wso2.carbon.apimgt.governance.api.model.ExtendedArtifactType;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicy;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyCategory;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyContent;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyInfo;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyList;
import org.wso2.carbon.apimgt.governance.api.model.APIMGovPolicyType;
import org.wso2.carbon.apimgt.governance.impl.APIMGovernanceConstants;
import org.wso2.carbon.apimgt.governance.impl.PolicyAttachmentManager;
import org.wso2.carbon.apimgt.governance.impl.PolicyManager;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class contains utility methods for Governance
 */
public class APIMGovernanceUtil {
    private static final Log log = LogFactory.getLog(APIMGovernanceUtil.class);

    /**
     * Generates a UUID
     *
     * @return UUID
     */
    public static String generateUUID() {

        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Get map from YAML string content
     *
     * @param content String content
     * @return Map
     * @throws APIMGovernanceException if an error occurs while parsing YAML content
     */
    public static Map<String, Object> getMapFromYAMLStringContent(String content) throws APIMGovernanceException {
        // Parse YAML content
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Map<String, Object> contentMap;
        try {
            contentMap = yamlReader.readValue(content, Map.class);
        } catch (JsonProcessingException e) {
            throw new APIMGovernanceException(APIMGovExceptionCodes.ERROR_FAILED_TO_PARSE_POLICY_CONTENT, e);
        }
        return contentMap;
    }

    /**
     * Resolves system properties and replaces in given in text
     *
     * @param text
     * @return System properties resolved text
     */
    public static String replaceSystemProperty(String text) {

        int indexOfStartingChars = -1;
        int indexOfClosingBrace;

        // The following condition deals with properties.
        // Properties are specified as ${system.property},
        // and are assumed to be System properties
        StringBuilder textBuilder = new StringBuilder(text);
        while (indexOfStartingChars < textBuilder.indexOf("${")
                && (indexOfStartingChars = textBuilder.indexOf("${")) != -1
                && (indexOfClosingBrace = textBuilder.toString().indexOf('}')) != -1) {

            String sysProp = textBuilder.substring(indexOfStartingChars + 2,
                    indexOfClosingBrace);
            String propValue = System.getProperty(sysProp);

            //Derive original text value with resolved system property value
            if (propValue != null) {
                textBuilder = new StringBuilder(textBuilder.substring(0, indexOfStartingChars) + propValue
                        + textBuilder.substring(indexOfClosingBrace + 1));
            }
            if ("carbon.home".equals(sysProp) && ".".equals(propValue)) {
                textBuilder.insert(0, new File(".").getAbsolutePath() + File.separator);
            }
        }
        text = textBuilder.toString();
        return text;
    }

    /**
     * Load default governance policies from the default policy directory
     *
     * @param organization Organization
     */
    public static void loadDefaultPolicies(String organization) {
        PolicyManager policyManager = new PolicyManager();
        try {
            // Fetch existing policies for the organization
            APIMGovPolicyList existingPolicies = policyManager.getPolicies(organization);
            List<APIMGovPolicyInfo> policyInfos = existingPolicies.getPolicyList();
            List<String> existingPolicyNames = policyInfos.stream()
                    .map(APIMGovPolicyInfo::getName)
                    .collect(Collectors.toList());

            // Define the path to default policies
            String pathToPolicies = CarbonUtils.getCarbonHome() + File.separator
                    + APIMGovernanceConstants.DEFAULT_POLICY_LOCATION;
            Path defaultPolicyPath = Paths.get(pathToPolicies);

            // Iterate through default policy files
            Files.list(defaultPolicyPath).forEach(path -> {
                File file = path.toFile();
                if (file.isFile() && (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml"))) {
                    try {
                        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                        DefaultGovPolicy defaultPolicy = mapper.readValue(file, DefaultGovPolicy.class);

                        // Add policy if it doesn't already exist
                        if (!existingPolicyNames.contains(defaultPolicy.getName())) {
                            log.info("Adding default policy: " + defaultPolicy.getName());
                            policyManager.createNewPolicy(
                                    getPolicyFromDefaultPolicy(defaultPolicy, file.getName()), organization);
                        } else {
                            log.info("Policy " + defaultPolicy.getName() + " already exists in organization: "
                                    + organization + "; skipping.");
                        }
                    } catch (IOException e) {
                        log.error("Error while loading default policy from file: " + file.getName(), e);
                    } catch (APIMGovernanceException e) {
                        log.error("Error while adding default policy: " + file.getName(), e);
                    }
                }
            });
        } catch (IOException e) {
            log.error("Error while accessing default policy directory", e);
        } catch (APIMGovernanceException e) {
            log.error("Error while retrieving existing policies for organization: " + organization, e);
        }
    }

    /**
     * Get Policy from DefaultPolicy
     *
     * @param defaultPolicy DefaultPolicy
     * @param fileName      File name
     * @return Governance Policy
     * @throws APIMGovernanceException if an error occurs while loading default policy content
     */
    public static APIMGovPolicy getPolicyFromDefaultPolicy(DefaultGovPolicy defaultPolicy,
                                                           String fileName) throws APIMGovernanceException {
        APIMGovPolicy policy = new APIMGovPolicy();
        policy.setName(defaultPolicy.getName());
        policy.setDescription(defaultPolicy.getDescription());
        policy.setPolicyCategory(APIMGovPolicyCategory.fromString(defaultPolicy.getPolicyCategory()));
        policy.setPolicyType(APIMGovPolicyType.fromString(defaultPolicy.getPolicyType()));
        policy.setArtifactType(ExtendedArtifactType.fromString(defaultPolicy.getArtifactType()));
        policy.setProvider(defaultPolicy.getProvider());
        policy.setDocumentationLink(defaultPolicy.getDocumentationLink());

        APIMGovPolicyContent policyContent = new APIMGovPolicyContent();
        policyContent.setFileName(fileName);
        policyContent.setContent(defaultPolicy.getPolicyContentString().getBytes(StandardCharsets.UTF_8));
        policy.setPolicyContent(policyContent);

        return policy;
    }

    /**
     * Get all artifacts for a given artifact type
     *
     * @param artifactType Artifact Type
     * @param organization Organization
     * @return List of artifact IDs
     * @throws APIMGovernanceException If an error occurs while getting the list of artifacts
     */
    public static List<String> getAllArtifacts(ArtifactType artifactType, String organization)
            throws APIMGovernanceException {
        if (ArtifactType.API.equals(artifactType)) {
            return APIMUtil.getAllAPIs(organization);
        }
        return new ArrayList<>();
    }

    /**
     * Get all artifacts as a map of Artifact Type, List of Artifact Reference IDs
     *
     * @param organization Organization
     * @return Map of Artifact Type, List of Artifact Reference IDs
     * @throws APIMGovernanceException If an error occurs while getting the list of artifacts
     */
    public static Map<ArtifactType, List<String>> getAllArtifacts(String organization) throws APIMGovernanceException {
        Map<ArtifactType, List<String>> artifacts = new HashMap<>();

        for (ArtifactType artifactType : ArtifactType.values()) {
            if (ArtifactType.API.equals(artifactType)) {
                List<String> artifactRefIds = APIMUtil.getAllAPIs(organization);
                artifacts.put(artifactType, artifactRefIds);
            }
        }

        return artifacts;
    }

    /**
     * Get artifacts for a label as a map of Artifact Type,
     * List of Artifact Reference IDs
     *
     * @param labelId Label ID
     * @return Map of Artifact Type, List of Artifact Reference IDs
     */
    public static Map<ArtifactType, List<String>> getArtifactsForLabel(String labelId) throws APIMGovernanceException {
        Map<ArtifactType, List<String>> artifacts = new HashMap<>();
        for (ArtifactType artifactType : ArtifactType.values()) {
            if (ArtifactType.API.equals(artifactType)) {
                List<String> artifactRefIds = APIMUtil.getAPIsByLabel(labelId);
                artifacts.put(artifactType, artifactRefIds);
            }
        }
        return artifacts;
    }

    /**
     * Get labels for an artifact
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType  Artifact Type
     * @return List of label IDs
     */
    public static List<String> getLabelsForArtifact(String artifactRefId, ArtifactType artifactType)
            throws APIMGovernanceException {
        List<String> labels = new ArrayList<>();
        if (ArtifactType.API.equals(artifactType)) {
            labels = APIMUtil.getLabelsForAPI(artifactRefId);
        }
        return labels;
    }

    /**
     * Get applicable policy attachments for an artifact
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType  Artifact Type
     * @param organization  Organization
     * @return Map of Policy Attachment IDs and Attachment Names
     */
    public static Map<String, String> getApplicablePolicyAttachmentsForArtifact(String artifactRefId,
                                                                                ArtifactType artifactType,
                                                                                String organization)
            throws APIMGovernanceException {

        List<String> labels = APIMGovernanceUtil.getLabelsForArtifact(artifactRefId, artifactType);
        PolicyAttachmentManager policyAttachmentManager = new PolicyAttachmentManager();

        Map<String, String> attachments = new HashMap<>();
        for (String label : labels) {
            Map<String, String> attachmentsForLabel = policyAttachmentManager
                    .getPolicyAttachmentsByLabel(label, organization);
            if (attachmentsForLabel != null) {
                attachments.putAll(attachmentsForLabel);
            }
        }

        attachments.putAll(policyAttachmentManager.getOrganizationWidePolicyAttachments(organization));

        return attachments; // Return a map of policy attachment IDs and attachment names
    }

    /**
     * Get all applicable policy attachment IDs for an artifact given a specific state at which
     * the artifact should be governed
     *
     * @param artifactRefId       Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType        Artifact Type
     * @param apimGovernableState Governable state (The state at which the artifact should be governed)
     * @param organization        Organization
     * @return List of applicable policy attachment IDs
     * @throws APIMGovernanceException if an error occurs while checking for applicable policy attachments
     */
    public static List<String> getApplicablePolicyAttachmentsForArtifactWithState(String artifactRefId,
                                                                                  ArtifactType artifactType,
                                                                                  APIMGovernableState
                                                                                          apimGovernableState,
                                                                                  String organization)
            throws APIMGovernanceException {

        List<String> labels = APIMGovernanceUtil.getLabelsForArtifact(artifactRefId, artifactType);
        PolicyAttachmentManager policyAttachmentManager = new PolicyAttachmentManager();

        // Check for policy attachments using labels and the state
        Set<String> policyAttachments = new HashSet<>();
        for (String label : labels) {
            // Get policy attachments for the label and state
            List<String> attachmentsForLabel = policyAttachmentManager
                    .getPolicyAttachmentByLabelAndState(label, apimGovernableState, organization);
            if (attachmentsForLabel != null) {
                policyAttachments.addAll(attachmentsForLabel);
            }
        }

        policyAttachments.addAll(policyAttachmentManager
                .getOrganizationWidePolicyAttachmentByState(apimGovernableState, organization));

        return new ArrayList<>(policyAttachments);
    }

    /**
     * Check for blocking actions in policy attachments
     *
     * @param policyAttachmentIds List of policy attachment IDs
     * @param governableState     Governable state
     * @param organization        Organization
     * @return boolean
     * @throws APIMGovernanceException if an error occurs while checking for blocking actions
     */
    public static boolean isBlockingActionsPresent(List<String> policyAttachmentIds,
                                                   APIMGovernableState governableState,
                                                   String organization)
            throws APIMGovernanceException {
        PolicyAttachmentManager policyAttachmentManager = new PolicyAttachmentManager();
        boolean isBlocking = false;
        for (String attachmentId : policyAttachmentIds) {
            if (policyAttachmentManager.isBlockingActionPresentForState(attachmentId, governableState, organization)) {
                isBlocking = true;
                break;
            }
        }
        return isBlocking;
    }

    /**
     * Check if an artifact is available
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType  Artifact Type
     * @return boolean
     */
    public static boolean isArtifactAvailable(String artifactRefId, ArtifactType artifactType) {
        artifactType = artifactType != null ? artifactType : ArtifactType.API;

        // Check if artifact exists in APIM
        boolean artifactExists = false;
        if (ArtifactType.API.equals(artifactType)) {
            artifactExists = APIMUtil.isAPIExist(artifactRefId);
        }
        return artifactExists;
    }

    /**
     * Get artifact name
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType  Artifact Type
     * @return String
     * @throws APIMGovernanceException If an error occurs while getting the artifact name
     */
    public static String getArtifactName(String artifactRefId, ArtifactType artifactType)
            throws APIMGovernanceException {

        String artifactName = null;
        if (ArtifactType.API.equals(artifactType)) {
            artifactName = APIMUtil.getAPIName(artifactRefId);
        }
        return artifactName;
    }

    /**
     * Get artifact version
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType  Artifact Type
     * @return String
     * @throws APIMGovernanceException If an error occurs while getting the artifact version
     */
    public static String getArtifactVersion(String artifactRefId, ArtifactType artifactType)
            throws APIMGovernanceException {

        String artifactVersion = null;
        if (ArtifactType.API.equals(artifactType)) {
            artifactVersion = APIMUtil.getAPIVersion(artifactRefId);
        }
        return artifactVersion;
    }

    /**
     * Get extended artifact type for an artifact
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType  Artifact Type
     * @return ExtendedArtifactType
     * @throws APIMGovernanceException If an error occurs while getting the extended artifact type
     */
    public static ExtendedArtifactType getExtendedArtifactTypeForArtifact
    (String artifactRefId, ArtifactType artifactType)
            throws APIMGovernanceException {
        if (ArtifactType.API.equals(artifactType)) {
            return APIMUtil.getExtendedArtifactTypeForAPI(APIMUtil.getAPIType(artifactRefId));
        }
        return null;
    }

    /**
     * Get artifact project
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param revisionNo    Revision Number
     * @param artifactType  Artifact Type
     * @param organization  Organization
     * @return byte[]
     * @throws APIMGovernanceException If an error occurs while getting the artifact project
     */
    public static byte[] getArtifactProjectWithRevision(String artifactRefId, String revisionNo,
                                                        ArtifactType artifactType,
                                                        String organization) throws APIMGovernanceException {

        // Get artifact project from APIM
        byte[] artifactProject = null;
        if (ArtifactType.API.equals(artifactType)) {
            artifactProject =
                    APIMUtil.getAPIProject(artifactRefId, revisionNo, organization);
        }
        return artifactProject;
    }

    /**
     * Get artifact project
     *
     * @param artifactRefId Artifact Reference ID (ID of the artifact on APIM side)
     * @param artifactType  Artifact Type
     * @param organization  Organization
     * @return byte[]
     * @throws APIMGovernanceException If an error occurs while getting the artifact project
     */
    public static byte[] getArtifactProject(String artifactRefId, ArtifactType artifactType,
                                            String organization) throws APIMGovernanceException {

        return getArtifactProjectWithRevision(artifactRefId, null, artifactType, organization);
    }

    /**
     * Extract project content into a map of RuleType and String
     *
     * @param project      Project
     * @param artifactType Artifact Type
     * @return Map of RuleType and String
     */
    public static Map<APIMGovPolicyType, String> extractArtifactProjectContent(byte[] project, ArtifactType artifactType)
            throws APIMGovernanceException {
        if (ArtifactType.API.equals(artifactType)) {
            return APIMUtil.extractAPIProjectContent(project);
        }
        return null;
    }
}
