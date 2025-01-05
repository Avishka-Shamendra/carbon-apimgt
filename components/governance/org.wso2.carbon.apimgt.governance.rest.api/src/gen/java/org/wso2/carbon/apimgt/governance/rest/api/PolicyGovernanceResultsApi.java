package org.wso2.carbon.apimgt.governance.rest.api;

import org.wso2.carbon.apimgt.governance.rest.api.dto.ErrorDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.PolicyGovernanceResultDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.PolicyGovernanceResultsDTO;
import org.wso2.carbon.apimgt.governance.rest.api.PolicyGovernanceResultsApiService;
import org.wso2.carbon.apimgt.governance.rest.api.impl.PolicyGovernanceResultsApiServiceImpl;
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
@Path("/policy-governance-results")

@Api(description = "the policy-governance-results API")




public class PolicyGovernanceResultsApi  {

  @Context MessageContext securityContext;

PolicyGovernanceResultsApiService delegate = new PolicyGovernanceResultsApiServiceImpl();


    @GET
    @Path("/{policyId}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieve governance results for a specific policy", notes = "Retrieve governance results associated with a specific governance policy within the organization using its unique ID.", response = PolicyGovernanceResultDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_result_read", description = "Read governance results")
        })
    }, tags={ "Governance Results",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with governance results for the specified policy.", response = PolicyGovernanceResultDTO.class),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response getGovernanceResultsByPolicyId(@ApiParam(value = "**UUID** of the Policy. ",required=true) @PathParam("policyId") String policyId) throws GovernanceException{
        return delegate.getGovernanceResultsByPolicyId(policyId, securityContext);
    }

    @GET
    
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieve governance results for all policies", notes = "Retrieve governance results of all governance policies within the organization.", response = PolicyGovernanceResultsDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:gov_result_read", description = "Read governance results")
        })
    }, tags={ "Governance Results" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with governance results for the specified policy.", response = PolicyGovernanceResultsDTO.class),
        @ApiResponse(code = 400, message = "Bad request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = ErrorDTO.class),
        @ApiResponse(code = 403, message = "Forbidden", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class) })
    public Response getGovernanceResultsForAllPolicies( @ApiParam(value = "Maximum size of resource array to return. ", defaultValue="25") @DefaultValue("25") @QueryParam("limit") Integer limit,  @ApiParam(value = "Starting point within the complete list of items qualified. ", defaultValue="0") @DefaultValue("0") @QueryParam("offset") Integer offset) throws GovernanceException{
        return delegate.getGovernanceResultsForAllPolicies(limit, offset, securityContext);
    }
}
