/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.gateway;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.gateway.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dto.EventHubConfigurationDto;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Logger manager to invoke the internal API and retrieve per API logging details.
 */
public class APILoggerManager {
    private static final Log log = LogFactory.getLog(APILoggerManager.class);
    private final Map<Map<String, String>, String> logProperties = new ConcurrentHashMap<>();
    private static final APILoggerManager apiLoggerManager = new APILoggerManager();
    private final EventHubConfigurationDto eventHubConfigurationDto;
    public static final String UTF8 = "UTF-8";

    public void initializeAPILoggerList(String organization) {
        try {
            String responseString = invokeService(organization);
            JSONObject responseJson = new JSONObject(responseString);
            JSONArray apiLogArray = responseJson.getJSONArray("apis");
            for (int i = 0; i < apiLogArray.length(); i++) {
                JSONObject apiLoggerObject = apiLogArray.getJSONObject(i);
                String resourceMethod = null;
                String resourcePath = null;
                if (!apiLoggerObject.isNull(APIConstants.METHOD_FOR_RESOURCE) && !apiLoggerObject.isNull(
                        APIConstants.PATH_FOR_RESOURCE)) {
                    resourceMethod = apiLoggerObject.getString(APIConstants.METHOD_FOR_RESOURCE);
                    resourcePath = apiLoggerObject.getString(APIConstants.PATH_FOR_RESOURCE);
                }
                Map<String, String> properties = new HashMap<>();
                properties.put(APIConstants.API_CONTEXT_FOR_RESOURCE,
                        apiLoggerObject.getString(APIConstants.API_CONTEXT_FOR_RESOURCE));
                properties.put(APIConstants.METHOD_FOR_RESOURCE, resourceMethod);
                properties.put(APIConstants.PATH_FOR_RESOURCE, resourcePath);
                logProperties.put(properties, apiLoggerObject.getString("logLevel"));
            }
            if (log.isDebugEnabled()) {
                log.debug("Response : " + responseString);
            }
        } catch (IOException | APIManagementException ex) {
            log.error("Error while calling internal service API", ex);
        }
    }

    public void updateLoggerMap(String apiContext, String logLevel, String resourceMethod, String resourcePath) {
        Map<String, String> properties = new HashMap<>();
        properties.put(APIConstants.API_CONTEXT_FOR_RESOURCE, apiContext);
        properties.put(APIConstants.PATH_FOR_RESOURCE, resourcePath);
        properties.put(APIConstants.METHOD_FOR_RESOURCE, resourceMethod);
        logProperties.put(properties, logLevel);
    }

    public Map<Map<String, String>, String> getPerAPILoggerList() {
        return logProperties;
    }

    private APILoggerManager() {
        this.eventHubConfigurationDto = ServiceReferenceHolder.getInstance().getApiManagerConfigurationService()
                .getAPIManagerConfiguration().getEventHubConfigurationDto();
    }

    public static APILoggerManager getInstance() {
        return apiLoggerManager;
    }

    private byte[] getServiceCredentials(EventHubConfigurationDto eventHubConfigurationDto) {

        String username = eventHubConfigurationDto.getUsername();
        String pw = eventHubConfigurationDto.getPassword();
        return Base64.encodeBase64((username + APIConstants.DELEM_COLON + pw).getBytes
                (StandardCharsets.UTF_8));
    }
    private String invokeService(String organization) throws IOException, APIManagementException {

        String serviceURLStr = eventHubConfigurationDto.getServiceUrl().concat(APIConstants.INTERNAL_WEB_APP_EP);
        HttpGet method = new HttpGet(serviceURLStr + "/api-logging-configs");

        URL serviceURL = new URL(serviceURLStr + "/api-logging-configs");
        byte[] credentials = getServiceCredentials(eventHubConfigurationDto);
        int servicePort = serviceURL.getPort();
        String serviceProtocol = serviceURL.getProtocol();
        method.setHeader(APIConstants.AUTHORIZATION_HEADER_DEFAULT, APIConstants.AUTHORIZATION_BASIC
                + new String(credentials, StandardCharsets.UTF_8));
        if (organization != null) {
            method.setHeader(APIConstants.HEADER_TENANT, organization);
        }
        HttpClient httpClient = APIUtil.getHttpClient(servicePort, serviceProtocol);
        try (CloseableHttpResponse httpResponse = APIUtil.executeHTTPRequestWithRetries(method, httpClient)){
            return EntityUtils.toString(httpResponse.getEntity(), UTF8);
        } catch (APIManagementException e) {
            throw new APIManagementException("Error while calling internal service", e);
        }



    }
}
