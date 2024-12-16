package org.wso2.carbon.apimgt.governance.rest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleContentDTO;
import javax.validation.constraints.*;

/**
 * Detailed information about a rule.
 **/

import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

@ApiModel(description = "Detailed information about a rule.")

public class RuleDTO   {
  
    private String id = null;
    private String name = null;
    private String description = null;

    @XmlType(name="AppliesToEnum")
    @XmlEnum(String.class)
    public enum AppliesToEnum {
        API_METADATA("API_METADATA"),
        API_DEFINITION("API_DEFINITION"),
        DOCUMENTATION("DOCUMENTATION");
        private String value;

        AppliesToEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AppliesToEnum fromValue(String v) {
            for (AppliesToEnum b : AppliesToEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private AppliesToEnum appliesTo = null;
    private List<String> pathList = new ArrayList<String>();
    private List<RuleContentDTO> ruleContent = new ArrayList<RuleContentDTO>();

    @XmlType(name="SeverityEnum")
    @XmlEnum(String.class)
    public enum SeverityEnum {
        ERROR("ERROR"),
        WARNING("WARNING"),
        INFORMATION("INFORMATION");
        private String value;

        SeverityEnum (String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SeverityEnum fromValue(String v) {
            for (SeverityEnum b : SeverityEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private SeverityEnum severity = null;
    private String message = null;
    private String provider = null;
    private String createdBy = null;
    private String createdTime = null;
    private String updatedBy = null;
    private String updatedTime = null;

  /**
   * UUID of the rule.
   **/
  public RuleDTO id(String id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(example = "123e4567-e89b-12d3-a456-426614174000", value = "UUID of the rule.")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Name of the rule.
   **/
  public RuleDTO name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(example = "API Name Rule", required = true, value = "Name of the rule.")
  @JsonProperty("name")
  @NotNull
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * A brief description of the rule.
   **/
  public RuleDTO description(String description) {
    this.description = description;
    return this;
  }

  
  @ApiModelProperty(example = "A rule designed to enforce API name length", value = "A brief description of the rule.")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Context or area to which the rule applies.
   **/
  public RuleDTO appliesTo(AppliesToEnum appliesTo) {
    this.appliesTo = appliesTo;
    return this;
  }

  
  @ApiModelProperty(example = "API_DEFINITION", required = true, value = "Context or area to which the rule applies.")
  @JsonProperty("appliesTo")
  @NotNull
  public AppliesToEnum getAppliesTo() {
    return appliesTo;
  }
  public void setAppliesTo(AppliesToEnum appliesTo) {
    this.appliesTo = appliesTo;
  }

  /**
   * List of json paths to which the rule applies.
   **/
  public RuleDTO pathList(List<String> pathList) {
    this.pathList = pathList;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "List of json paths to which the rule applies.")
  @JsonProperty("pathList")
  @NotNull
  public List<String> getPathList() {
    return pathList;
  }
  public void setPathList(List<String> pathList) {
    this.pathList = pathList;
  }

  /**
   * List of functions and parameters that define the rule.
   **/
  public RuleDTO ruleContent(List<RuleContentDTO> ruleContent) {
    this.ruleContent = ruleContent;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "List of functions and parameters that define the rule.")
      @Valid
  @JsonProperty("ruleContent")
  @NotNull
  public List<RuleContentDTO> getRuleContent() {
    return ruleContent;
  }
  public void setRuleContent(List<RuleContentDTO> ruleContent) {
    this.ruleContent = ruleContent;
  }

  /**
   * Severity level of the rule.
   **/
  public RuleDTO severity(SeverityEnum severity) {
    this.severity = severity;
    return this;
  }

  
  @ApiModelProperty(example = "WARNING", required = true, value = "Severity level of the rule.")
  @JsonProperty("severity")
  @NotNull
  public SeverityEnum getSeverity() {
    return severity;
  }
  public void setSeverity(SeverityEnum severity) {
    this.severity = severity;
  }

  /**
   * Message to be displayed when the rule is violated.
   **/
  public RuleDTO message(String message) {
    this.message = message;
    return this;
  }

  
  @ApiModelProperty(example = "API name can not be too long or short", value = "Message to be displayed when the rule is violated.")
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Entity or individual providing the rule.
   **/
  public RuleDTO provider(String provider) {
    this.provider = provider;
    return this;
  }

  
  @ApiModelProperty(example = "TechWave", value = "Entity or individual providing the rule.")
  @JsonProperty("provider")
  public String getProvider() {
    return provider;
  }
  public void setProvider(String provider) {
    this.provider = provider;
  }

  /**
   * Identifier of the user who created the rule.
   **/
  public RuleDTO createdBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  
  @ApiModelProperty(example = "admin@wso2.com", value = "Identifier of the user who created the rule.")
  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Timestamp when the rule was created.
   **/
  public RuleDTO createdTime(String createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  
  @ApiModelProperty(example = "2024-08-01T12:00:00Z", value = "Timestamp when the rule was created.")
  @JsonProperty("createdTime")
  public String getCreatedTime() {
    return createdTime;
  }
  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }

  /**
   * Identifier of the user who last updated the rule.
   **/
  public RuleDTO updatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
    return this;
  }

  
  @ApiModelProperty(example = "admin@wso2.com", value = "Identifier of the user who last updated the rule.")
  @JsonProperty("updatedBy")
  public String getUpdatedBy() {
    return updatedBy;
  }
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  /**
   * Timestamp when the rule was last updated.
   **/
  public RuleDTO updatedTime(String updatedTime) {
    this.updatedTime = updatedTime;
    return this;
  }

  
  @ApiModelProperty(example = "2024-08-10T12:00:00Z", value = "Timestamp when the rule was last updated.")
  @JsonProperty("updatedTime")
  public String getUpdatedTime() {
    return updatedTime;
  }
  public void setUpdatedTime(String updatedTime) {
    this.updatedTime = updatedTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleDTO rule = (RuleDTO) o;
    return Objects.equals(id, rule.id) &&
        Objects.equals(name, rule.name) &&
        Objects.equals(description, rule.description) &&
        Objects.equals(appliesTo, rule.appliesTo) &&
        Objects.equals(pathList, rule.pathList) &&
        Objects.equals(ruleContent, rule.ruleContent) &&
        Objects.equals(severity, rule.severity) &&
        Objects.equals(message, rule.message) &&
        Objects.equals(provider, rule.provider) &&
        Objects.equals(createdBy, rule.createdBy) &&
        Objects.equals(createdTime, rule.createdTime) &&
        Objects.equals(updatedBy, rule.updatedBy) &&
        Objects.equals(updatedTime, rule.updatedTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, appliesTo, pathList, ruleContent, severity, message, provider, createdBy, createdTime, updatedBy, updatedTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuleDTO {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    appliesTo: ").append(toIndentedString(appliesTo)).append("\n");
    sb.append("    pathList: ").append(toIndentedString(pathList)).append("\n");
    sb.append("    ruleContent: ").append(toIndentedString(ruleContent)).append("\n");
    sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    createdTime: ").append(toIndentedString(createdTime)).append("\n");
    sb.append("    updatedBy: ").append(toIndentedString(updatedBy)).append("\n");
    sb.append("    updatedTime: ").append(toIndentedString(updatedTime)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

