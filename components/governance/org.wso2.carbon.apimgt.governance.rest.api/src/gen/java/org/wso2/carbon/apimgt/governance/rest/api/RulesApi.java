package org.wso2.carbon.apimgt.governance.rest.api;

import org.wso2.carbon.apimgt.governance.rest.api.dto.ErrorDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleInfoDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RulesListDTO;
import org.wso2.carbon.apimgt.governance.rest.api.RulesApiService;
import org.wso2.carbon.apimgt.governance.rest.api.impl.RulesApiServiceImpl;
import org.wso2.carbon.apimgt.governance.api.error.GovernanceException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.inject.Inject;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
@Path("/rules")

@Api(description = "the rules API")




public class RulesApi  {

  @Context MessageContext securityContext;

RulesApiService delegate = new RulesApiServiceImpl();


    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Create a new rule.", notes = "Creates a new rule in the user's organization.", response = RuleInfoDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_rule_manage", description = "Manage governance rules")
        })
    }, tags={ "Rules",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "OK. Rule created successfully.", response = RuleInfoDTO.class),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response createRule(@ApiParam(value = "JSON object containing the details of the new rule." ,required=true) RuleDTO ruleDTO) throws GovernanceException{
        return delegate.createRule(ruleDTO, securityContext);
    }

    @DELETE
    @Path("/{ruleId}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Delete a rule by ID.", notes = "Deletes a rule from the user's organization.", response = Void.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_rule_manage", description = "Manage governance rules")
        })
    }, tags={ "Rules",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Rule deleted successfully.", response = Void.class),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Rule not found", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response deleteRuleById(@ApiParam(value = "ID of the rule to delete.",required=true) @PathParam("ruleId") String ruleId) throws GovernanceException{
        return delegate.deleteRuleById(ruleId, securityContext);
    }

    @GET
    @Path("/{ruleId}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieves a rule by ID.", notes = "Returns detailed information about a rule.", response = RuleDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_rule_read", description = "Read governance rules")
        })
    }, tags={ "Rules",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Successful response with the rule details.", response = RuleDTO.class),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Rule not found", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response getRuleById(@ApiParam(value = "ID of the rule to retrieve.",required=true) @PathParam("ruleId") String ruleId) throws GovernanceException{
        return delegate.getRuleById(ruleId, securityContext);
    }

    @GET
    @Path("/{ruleId}/usage")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieves usage of each rule.", notes = "Retrieves the list of policies which use the rule.", response = String.class, responseContainer = "List", authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_rule_read", description = "Read governance rules")
        })
    }, tags={ "Rules",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Rule usage retrieved successfully.", response = String.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response getRuleUsage(@ApiParam(value = "UUID of the rule.",required=true) @PathParam("ruleId") String ruleId) throws GovernanceException{
        return delegate.getRuleUsage(ruleId, securityContext);
    }

    @GET
    
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieves a list of rules.", notes = "Returns a list of all rules associated with the requested organization.", response = RulesListDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_rule_read", description = "Read governance rules")
        })
    }, tags={ "Rules",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Successful response with a list of rules.", response = RulesListDTO.class),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response getRules() throws GovernanceException{
        return delegate.getRules(securityContext);
    }

    @PUT
    @Path("/{ruleId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Update a rule by ID.", notes = "Updates the details of a rule.", response = RuleInfoDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_rule_manage", description = "Manage governance rules")
        })
    }, tags={ "Rules" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Rule updated successfully.", response = RuleInfoDTO.class),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Rule not found", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response updateRuleById(@ApiParam(value = "ID of the rule to update.",required=true) @PathParam("ruleId") String ruleId, @ApiParam(value = "JSON object containing the updated details of the rule." ,required=true) RuleDTO ruleDTO) throws GovernanceException{
        return delegate.updateRuleById(ruleId, ruleDTO, securityContext);
    }
}
