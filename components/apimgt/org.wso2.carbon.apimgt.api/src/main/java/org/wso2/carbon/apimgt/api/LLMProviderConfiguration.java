package org.wso2.carbon.apimgt.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class LLMProviderConfiguration {

    @JsonProperty("connectorType")
    private String connectorType;

    @JsonProperty("metadata")
    private List<LLMProviderMetadata> metadata;

    @JsonProperty("additionalHeaders")
    private List<String> additionalHeaders;

    @JsonProperty("additionalQueryParameters")
    private List<String> additionalQueryParameters;

    public LLMProviderConfiguration() {}

    @JsonCreator
    public LLMProviderConfiguration(
            @JsonProperty("connectorType") String connectorType,
            @JsonProperty("metadata") List<LLMProviderMetadata> metadata,
            @JsonProperty("additionalHeaders") List<String> additionalHeaders,
            @JsonProperty("additionalQueryParameters") List<String> additionalQueryParameters) {

        this.connectorType = connectorType;
        this.metadata = metadata;
        this.additionalHeaders = additionalHeaders;
        this.additionalQueryParameters = additionalQueryParameters;
    }

    public String getConnectorType() {

        return connectorType;
    }

    public void setConnectorType(String connectorType) {

        this.connectorType = connectorType;
    }

    public List<LLMProviderMetadata> getMetadata() {

        return metadata;
    }

    public void setMetadata(List<LLMProviderMetadata> metadata) {

        this.metadata = metadata;
    }

    public List<String> getAdditionalHeaders() {

        return additionalHeaders;
    }

    public void setAdditionalHeaders(List<String> additionalHeaders) {

        this.additionalHeaders = additionalHeaders;
    }

    public List<String> getAdditionalQueryParameters() {

        return additionalQueryParameters;
    }

    public void setAdditionalQueryParameters(List<String> additionalQueryParameters) {

        this.additionalQueryParameters = additionalQueryParameters;
    }

    public String toJsonString() throws APIManagementException {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            throw new APIManagementException("Error occurred while parsing LLM Provider configuration");
        }
    }
}