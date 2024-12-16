package org.wso2.carbon.apimgt.governance.rest.api;

import org.wso2.carbon.apimgt.governance.rest.api.*;
import org.wso2.carbon.apimgt.governance.rest.api.dto.*;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import org.wso2.carbon.apimgt.governance.api.error.GovernanceException;

import org.wso2.carbon.apimgt.governance.rest.api.dto.ErrorDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleInfoDTO;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RulesListDTO;

import java.util.List;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


public interface RulesApiService {
      public Response createRule(RuleDTO ruleDTO, MessageContext messageContext) throws GovernanceException;
      public Response deleteRuleById(String ruleId, MessageContext messageContext) throws GovernanceException;
      public Response getRuleById(String ruleId, MessageContext messageContext) throws GovernanceException;
      public Response getRuleUsage(String ruleId, MessageContext messageContext) throws GovernanceException;
      public Response getRules(MessageContext messageContext) throws GovernanceException;
      public Response updateRuleById(String ruleId, RuleDTO ruleDTO, MessageContext messageContext) throws GovernanceException;
}
