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

package org.wso2.carbon.apimgt.governance.rest.api.mappings;

import org.wso2.carbon.apimgt.governance.api.model.Rule;
import org.wso2.carbon.apimgt.governance.api.model.RuleContent;
import org.wso2.carbon.apimgt.governance.api.model.RuleInfo;
import org.wso2.carbon.apimgt.governance.api.model.RuleList;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleContentDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleInfoDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleListDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Rule Mapping Utility
 */
public class RuleMappingUtil {


    /**
     * Converts RuleDTO object to Rule object
     *
     * @param ruleDTO RuleDTO object
     * @return Rule object
     */
    public static Rule fromRuleDTOtoRule(RuleDTO ruleDTO) {
        Rule rule = new Rule();
        rule.setId(ruleDTO.getId());
        rule.setName(ruleDTO.getName());
        rule.setDescription(ruleDTO.getDescription());
        rule.setAppliesTo(String.valueOf(ruleDTO.getAppliesTo()));
        rule.setPaths(ruleDTO.getPathList());
        rule.setSeverity(String.valueOf(ruleDTO.getSeverity()));
        List<RuleContent> ruleContent = new ArrayList<>();
        for (RuleContentDTO ruleContentDTO : ruleDTO.getRuleContent()) {
            ruleContent.add(fromRuleContentDTOtoRuleContent(ruleContentDTO));
        }
        rule.setMessage(ruleDTO.getMessage());
        rule.setRuleContent(ruleContent);
        rule.setProvider(ruleDTO.getProvider());
        rule.setCreatedBy(ruleDTO.getCreatedBy());
        rule.setCreatedTime(ruleDTO.getCreatedTime());
        rule.setUpdatedBy(ruleDTO.getUpdatedBy());
        rule.setUpdatedTime(ruleDTO.getUpdatedTime());
        return rule;
    }

    /**
     * Converts a RuleContentDTO object to a RuleContent object
     *
     * @param ruleContentDTO RuleContentDTO object
     * @return RuleContent object
     */
    public static RuleContent fromRuleContentDTOtoRuleContent(RuleContentDTO ruleContentDTO) {
        RuleContent ruleContent = new RuleContent();
        ruleContent.setFunction(ruleContentDTO.getFunction());
        ruleContent.setParameters(ruleContentDTO.getParameters());
        return ruleContent;
    }

    /**
     * Converts a Rule object to a RuleDTO object
     *
     * @param createdRule Rule object
     * @return RuleDTO object
     */
    public static RuleDTO fromRuleToRuleDTO(Rule createdRule) {
        RuleDTO ruleDTO = new RuleDTO();
        ruleDTO.setId(createdRule.getId());
        ruleDTO.setName(createdRule.getName());
        ruleDTO.setDescription(createdRule.getDescription());
        ruleDTO.setAppliesTo(RuleDTO.AppliesToEnum.
                fromValue(createdRule.getAppliesTo()));
        ruleDTO.setPathList(createdRule.getPaths());
        ruleDTO.setSeverity(RuleDTO.SeverityEnum.
                fromValue(createdRule.getSeverity()));
        List<RuleContentDTO> ruleContentDTOList = new ArrayList<>();
        for (RuleContent ruleContent : createdRule.getRuleContent()) {
            ruleContentDTOList.add(fromRuleContentToRuleContentDTO(ruleContent));
        }
        ruleDTO.setMessage(createdRule.getMessage());
        ruleDTO.setRuleContent(ruleContentDTOList);
        ruleDTO.setProvider(createdRule.getProvider());
        ruleDTO.setCreatedBy(createdRule.getCreatedBy());
        ruleDTO.setCreatedTime(createdRule.getCreatedTime());
        ruleDTO.setUpdatedBy(createdRule.getUpdatedBy());
        ruleDTO.setUpdatedTime(createdRule.getUpdatedTime());
        return ruleDTO;
    }

    /**
     * Converts a RuleContentDTO object to a RuleContent object
     *
     * @param ruleContent RuleContent object
     * @return RuleContentDTO object
     */
    public static RuleContentDTO fromRuleContentToRuleContentDTO(RuleContent ruleContent) {
        RuleContentDTO ruleContentDTO = new RuleContentDTO();
        ruleContentDTO.setFunction(ruleContent.getFunction());
        ruleContentDTO.setParameters(ruleContent.getParameters());
        return ruleContentDTO;
    }


    /**
     * Converts a RuleInfo object to a RuleInfoDTO object
     *
     * @param ruleInfo RuleInfo object
     * @return RuleInfoDTO object
     */
    public static RuleInfoDTO fromRuleInfoToRuleInfoDTO(RuleInfo ruleInfo) {
        RuleInfoDTO ruleInfoDTO = new RuleInfoDTO();
        ruleInfoDTO.setId(ruleInfo.getId());
        ruleInfoDTO.setName(ruleInfo.getName());
        ruleInfoDTO.setDescription(ruleInfo.getDescription());
        ruleInfoDTO.setAppliesTo(RuleInfoDTO.
                AppliesToEnum.fromValue(ruleInfo.getAppliesTo()));
        ruleInfoDTO.setSeverity(RuleInfoDTO.
                SeverityEnum.fromValue(ruleInfo.getSeverity()));
        ruleInfoDTO.setProvider(ruleInfo.getProvider());
        return ruleInfoDTO;
    }

    /**
     * Converts a RuleList object to a RuleListDTO object
     *
     * @param ruleList RuleList object
     * @return RuleListDTO object
     */
    public static RuleListDTO fromRuleListToRuleListDTO(RuleList ruleList) {
        RuleListDTO ruleListDTO = new RuleListDTO();
        List<RuleInfoDTO> ruleInfoDTOList = new ArrayList<>();
        for (RuleInfo ruleInfo : ruleList.getRulesList()) {
            ruleInfoDTOList.add(fromRuleInfoToRuleInfoDTO(ruleInfo));
        }
        ruleListDTO.setList(ruleInfoDTOList);
        return ruleListDTO;
    }
}
