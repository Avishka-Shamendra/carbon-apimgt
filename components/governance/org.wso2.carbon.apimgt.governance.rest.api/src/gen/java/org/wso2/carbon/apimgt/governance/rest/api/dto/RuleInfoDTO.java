package org.wso2.carbon.apimgt.governance.rest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

/**
 * Summary information about a rule.
 **/

import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

@ApiModel(description = "Summary information about a rule.")

public class RuleInfoDTO   {
  
    private String id = null;
    private String name = null;
    private String description = null;
    private String provider = null;

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

  /**
   * UUID of the rule.
   **/
  public RuleInfoDTO id(String id) {
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
  public RuleInfoDTO name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(example = "API Name Rule", value = "Name of the rule.")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * A brief description of the rule.
   **/
  public RuleInfoDTO description(String description) {
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
   * Entity or individual providing the rule.
   **/
  public RuleInfoDTO provider(String provider) {
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
   * Context or area to which the rule applies.
   **/
  public RuleInfoDTO appliesTo(AppliesToEnum appliesTo) {
    this.appliesTo = appliesTo;
    return this;
  }

  
  @ApiModelProperty(example = "API_DEFINITION", value = "Context or area to which the rule applies.")
  @JsonProperty("appliesTo")
  public AppliesToEnum getAppliesTo() {
    return appliesTo;
  }
  public void setAppliesTo(AppliesToEnum appliesTo) {
    this.appliesTo = appliesTo;
  }

  /**
   * List of json paths to which the rule applies.
   **/
  public RuleInfoDTO pathList(List<String> pathList) {
    this.pathList = pathList;
    return this;
  }

  
  @ApiModelProperty(value = "List of json paths to which the rule applies.")
  @JsonProperty("pathList")
  public List<String> getPathList() {
    return pathList;
  }
  public void setPathList(List<String> pathList) {
    this.pathList = pathList;
  }

  /**
   * Severity level of the rule.
   **/
  public RuleInfoDTO severity(SeverityEnum severity) {
    this.severity = severity;
    return this;
  }

  
  @ApiModelProperty(example = "WARNING", value = "Severity level of the rule.")
  @JsonProperty("severity")
  public SeverityEnum getSeverity() {
    return severity;
  }
  public void setSeverity(SeverityEnum severity) {
    this.severity = severity;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleInfoDTO ruleInfo = (RuleInfoDTO) o;
    return Objects.equals(id, ruleInfo.id) &&
        Objects.equals(name, ruleInfo.name) &&
        Objects.equals(description, ruleInfo.description) &&
        Objects.equals(provider, ruleInfo.provider) &&
        Objects.equals(appliesTo, ruleInfo.appliesTo) &&
        Objects.equals(pathList, ruleInfo.pathList) &&
        Objects.equals(severity, ruleInfo.severity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, provider, appliesTo, pathList, severity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuleInfoDTO {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    appliesTo: ").append(toIndentedString(appliesTo)).append("\n");
    sb.append("    pathList: ").append(toIndentedString(pathList)).append("\n");
    sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
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

