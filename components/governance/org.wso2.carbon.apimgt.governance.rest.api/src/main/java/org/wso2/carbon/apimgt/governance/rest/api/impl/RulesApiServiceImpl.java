package org.wso2.carbon.apimgt.governance.rest.api.impl;

import org.wso2.carbon.apimgt.governance.api.GovernanceAPIConstants;
import org.wso2.carbon.apimgt.governance.api.error.GovernanceException;
import org.wso2.carbon.apimgt.governance.api.error.GovernanceExceptionCodes;
import org.wso2.carbon.apimgt.governance.api.manager.RuleManager;
import org.wso2.carbon.apimgt.governance.api.model.Rule;

import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.governance.api.model.RuleInfo;
import org.wso2.carbon.apimgt.governance.api.model.RuleList;
import org.wso2.carbon.apimgt.governance.rest.api.RulesApiService;
import org.wso2.carbon.apimgt.governance.rest.api.dto.ErrorDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleInfoDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleListDTO;
import org.wso2.carbon.apimgt.governance.rest.api.mappings.RuleMappingUtil;
import org.wso2.carbon.apimgt.governance.rest.api.util.GovernanceAPIUtil;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

/**
 * This is the implementation class for the Rules API.
 */
public class RulesApiServiceImpl implements RulesApiService {

    /**
     * Create a new Governance Rule
     *
     * @param ruleDTO        Rule object
     * @param messageContext MessageContext
     * @return Response object
     * @throws GovernanceException If an error occurs while creating the rule
     */
    @Override
    public Response createRule(RuleDTO ruleDTO, MessageContext messageContext) throws GovernanceException {
        RuleDTO createdRuleDTO;
        URI createdRuleURI;
        try {
            String username = GovernanceAPIUtil.getLoggedInUsername();
            String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);

            Rule rule = RuleMappingUtil.fromRuleDTOtoRule(ruleDTO);
            rule.setCreatedBy(username);

            RuleManager ruleManager = new RuleManagerImpl();
            Rule createdRule = ruleManager.createNewRule(organization, rule);

            createdRuleDTO = RuleMappingUtil.fromRuleToRuleDTO(createdRule);
            createdRuleURI = new URI(
                    GovernanceAPIConstants.RULE_PATH + "/" + createdRuleDTO.getId());
            return Response.created(createdRuleURI).entity(createdRuleDTO).build();

        } catch (URISyntaxException | GovernanceException e) {
            String error = String.format("Error while creating URI for new Rule %s",
                    ruleDTO.getName());
            throw new GovernanceException(error, e, GovernanceExceptionCodes.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a Governance Rule by ID
     *
     * @param ruleId         Rule ID
     * @param messageContext MessageContext
     * @return Response object
     * @throws GovernanceException If an error occurs while deleting the rule
     */
    @Override
    public Response deleteRuleById(String ruleId, MessageContext messageContext) throws GovernanceException {
        RuleManager ruleManager = new RuleManagerImpl();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);

        ruleManager.deleteRule(organization, ruleId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Get a Governance Rule by ID
     *
     * @param ruleId         Rule ID
     * @param messageContext MessageContext
     * @return Response object
     */
    @Override
    public Response getRuleById(String ruleId, MessageContext messageContext) throws GovernanceException {
        RuleManager ruleManager = new RuleManagerImpl();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);

        RuleInfo rule = ruleManager.getRuleById(organization, ruleId);
        RuleInfoDTO ruleInfoDTO = RuleMappingUtil.fromRuleInfoToRuleInfoDTO(rule);
        return Response.status(Response.Status.OK).entity(ruleInfoDTO).build();
    }

    public Response getRuleUsage(String ruleId, MessageContext messageContext) {
        // remove errorObject and add implementation code!
        ErrorDTO errorObject = new ErrorDTO();
        Response.Status status = Response.Status.NOT_IMPLEMENTED;
        errorObject.setCode((long) status.getStatusCode());
        errorObject.setMessage(status.toString());
        errorObject.setDescription("The requested resource has not been implemented");
        return Response.status(status).entity(errorObject).build();
    }

    /**
     * Get all Governance Rules for a specific organization
     *
     * @param messageContext MessageContext
     * @return Response object
     */
    @Override
    public Response getRules(MessageContext messageContext) throws GovernanceException {
        RuleManager ruleManager = new RuleManagerImpl();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);

        RuleList ruleList = ruleManager.getRules(organization);
        RuleListDTO ruleListDTO = RuleMappingUtil.fromRuleListToRuleListDTO(ruleList);
        return Response.status(Response.Status.OK).entity(ruleListDTO).build();
    }

    /**
     * Update a Governance Rule by ID
     *
     * @param ruleId         Rule ID
     * @param ruleDTO        Rule object
     * @param messageContext MessageContext
     * @return Response object
     */
    @Override
    public Response updateRuleById(String ruleId, RuleDTO ruleDTO, MessageContext messageContext) throws GovernanceException {
        String username = GovernanceAPIUtil.getLoggedInUsername();
        String organization = GovernanceAPIUtil.getValidatedOrganization(messageContext);

        Rule rule = RuleMappingUtil.fromRuleDTOtoRule(ruleDTO);
        rule.setUpdatedBy(username);
        rule.setId(ruleId);

        RuleManager ruleManager = new RuleManagerImpl();
        rule = ruleManager.updateRule(organization, ruleId, rule);
        // TODO: Re-trigger rule(policy) run
        return Response.status(Response.Status.OK).entity(RuleMappingUtil.
                fromRuleToRuleDTO(rule)).build();
    }
}
