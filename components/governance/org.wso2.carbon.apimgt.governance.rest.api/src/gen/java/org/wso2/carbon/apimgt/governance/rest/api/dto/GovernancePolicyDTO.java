package org.wso2.carbon.apimgt.governance.rest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

/**
 * Detailed information about a governance policy.
 **/

import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

@ApiModel(description = "Detailed information about a governance policy.")

public class GovernancePolicyDTO   {
  
    private String id = null;
    private String name = null;
    private String description = null;

    @XmlType(name="List&lt;ApplicableArtifactTypesEnum&gt;")
    @XmlEnum(List&lt;String&gt;.class)
    public enum List&lt;ApplicableArtifactTypesEnum&gt; {
        REST_API("REST_API"),
        SOAP_API("SOAP_API"),
        GRAPHQL_API("GRAPHQL_API"),
        ASYNC_API("ASYNC_API");
        private List&lt;String&gt; value;

        List&lt;ApplicableArtifactTypesEnum&gt; (List&lt;String&gt; v) {
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
        public static List&lt;ApplicableArtifactTypesEnum&gt; fromValue(String v) {
            for (List<ApplicableArtifactTypesEnum> b : List<ApplicableArtifactTypesEnum>.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }

    @XmlType(name="ApplicableArtifactTypesEnum")
    @XmlEnum(String.class)
    public enum ApplicableArtifactTypesEnum {
        REST_API("REST_API"),
        SOAP_API("SOAP_API"),
        GRAPHQL_API("GRAPHQL_API"),
        ASYNC_API("ASYNC_API");
        private String value;

        ApplicableArtifactTypesEnum (String v) {
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
        public static ApplicableArtifactTypesEnum fromValue(String v) {
            for (ApplicableArtifactTypesEnum b : ApplicableArtifactTypesEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private List<ApplicableArtifactTypesEnum> applicableArtifactTypes = new ArrayList<ApplicableArtifactTypesEnum>();

    @XmlType(name="List&lt;ApplicableStatesEnum&gt;")
    @XmlEnum(List&lt;String&gt;.class)
    public enum List&lt;ApplicableStatesEnum&gt; {
        CREATION("API_CREATION"),
        DEPLOYMENT("API_DEPLOYMENT"),
        PUBLICATION("API_PUBLICATION");
        private List&lt;String&gt; value;

        List&lt;ApplicableStatesEnum&gt; (List&lt;String&gt; v) {
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
        public static List&lt;ApplicableStatesEnum&gt; fromValue(String v) {
            for (List<ApplicableStatesEnum> b : List<ApplicableStatesEnum>.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }

    @XmlType(name="ApplicableStatesEnum")
    @XmlEnum(String.class)
    public enum ApplicableStatesEnum {
        CREATION("API_CREATION"),
        DEPLOYMENT("API_DEPLOYMENT"),
        PUBLICATION("API_PUBLICATION");
        private String value;

        ApplicableStatesEnum (String v) {
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
        public static ApplicableStatesEnum fromValue(String v) {
            for (ApplicableStatesEnum b : ApplicableStatesEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private List<ApplicableStatesEnum> applicableStates = new ArrayList<ApplicableStatesEnum>();

    @XmlType(name="ActionOnViolationEnum")
    @XmlEnum(String.class)
    public enum ActionOnViolationEnum {
        BLOCK("BLOCK"),
        WARN("WARN");
        private String value;

        ActionOnViolationEnum (String v) {
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
        public static ActionOnViolationEnum fromValue(String v) {
            for (ActionOnViolationEnum b : ActionOnViolationEnum.values()) {
                if (String.valueOf(b.value).equals(v)) {
                    return b;
                }
            }
return null;
        }
    }
    private ActionOnViolationEnum actionOnViolation = null;
    private List<String> rules = new ArrayList<String>();
    private List<String> labels = new ArrayList<String>();
    private String createdBy = null;
    private String createdTime = null;
    private String updatedBy = null;
    private String updatedTime = null;

  /**
   * UUID of the governance policy.
   **/
  public GovernancePolicyDTO id(String id) {
    this.id = id;
    return this;
  }

  
  @ApiModelProperty(example = "987e6543-d21b-34d5-b678-912345678900", value = "UUID of the governance policy.")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Name of the governance policy.
   **/
  public GovernancePolicyDTO name(String name) {
    this.name = name;
    return this;
  }

  
  @ApiModelProperty(example = "API Security Policy", required = true, value = "Name of the governance policy.")
  @JsonProperty("name")
  @NotNull
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * A brief description of the governance policy.
   **/
  public GovernancePolicyDTO description(String description) {
    this.description = description;
    return this;
  }

  
  @ApiModelProperty(example = "Policy for enforcing security standards across all APIs.", value = "A brief description of the governance policy.")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * List of artifact types to which the governance policy applies.
   **/
  public GovernancePolicyDTO applicableArtifactTypes(List<ApplicableArtifactTypesEnum> applicableArtifactTypes) {
    this.applicableArtifactTypes = applicableArtifactTypes;
    return this;
  }

  
  @ApiModelProperty(value = "List of artifact types to which the governance policy applies.")
  @JsonProperty("applicableArtifactTypes")
  public List<ApplicableArtifactTypesEnum> getApplicableArtifactTypes() {
    return applicableArtifactTypes;
  }
  public void setApplicableArtifactTypes(List<ApplicableArtifactTypesEnum> applicableArtifactTypes) {
    this.applicableArtifactTypes = applicableArtifactTypes;
  }

  /**
   * List of states at which the governance policy should be enforced.
   **/
  public GovernancePolicyDTO applicableStates(List<ApplicableStatesEnum> applicableStates) {
    this.applicableStates = applicableStates;
    return this;
  }

  
  @ApiModelProperty(value = "List of states at which the governance policy should be enforced.")
  @JsonProperty("applicableStates")
  public List<ApplicableStatesEnum> getApplicableStates() {
    return applicableStates;
  }
  public void setApplicableStates(List<ApplicableStatesEnum> applicableStates) {
    this.applicableStates = applicableStates;
  }

  /**
   * Action to be taken when the governance policy is violated.
   **/
  public GovernancePolicyDTO actionOnViolation(ActionOnViolationEnum actionOnViolation) {
    this.actionOnViolation = actionOnViolation;
    return this;
  }

  
  @ApiModelProperty(example = "BLOCK", value = "Action to be taken when the governance policy is violated.")
  @JsonProperty("actionOnViolation")
  public ActionOnViolationEnum getActionOnViolation() {
    return actionOnViolation;
  }
  public void setActionOnViolation(ActionOnViolationEnum actionOnViolation) {
    this.actionOnViolation = actionOnViolation;
  }

  /**
   * List of rules associated with the governance policy.
   **/
  public GovernancePolicyDTO rules(List<String> rules) {
    this.rules = rules;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "List of rules associated with the governance policy.")
  @JsonProperty("rules")
  @NotNull
  public List<String> getRules() {
    return rules;
  }
  public void setRules(List<String> rules) {
    this.rules = rules;
  }

  /**
   * Labels associated with the governance policy.
   **/
  public GovernancePolicyDTO labels(List<String> labels) {
    this.labels = labels;
    return this;
  }

  
  @ApiModelProperty(example = "[\"security\"]", value = "Labels associated with the governance policy.")
  @JsonProperty("labels")
  public List<String> getLabels() {
    return labels;
  }
  public void setLabels(List<String> labels) {
    this.labels = labels;
  }

  /**
   * Identifier of the user who created the governance policy.
   **/
  public GovernancePolicyDTO createdBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  
  @ApiModelProperty(example = "admin@wso2.com", value = "Identifier of the user who created the governance policy.")
  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Timestamp when the governance policy was created.
   **/
  public GovernancePolicyDTO createdTime(String createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  
  @ApiModelProperty(example = "2024-08-01T12:00:00Z", value = "Timestamp when the governance policy was created.")
  @JsonProperty("createdTime")
  public String getCreatedTime() {
    return createdTime;
  }
  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }

  /**
   * Identifier of the user who last updated the governance policy.
   **/
  public GovernancePolicyDTO updatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
    return this;
  }

  
  @ApiModelProperty(example = "admin@wso2.com", value = "Identifier of the user who last updated the governance policy.")
  @JsonProperty("updatedBy")
  public String getUpdatedBy() {
    return updatedBy;
  }
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  /**
   * Timestamp when the governance policy was last updated.
   **/
  public GovernancePolicyDTO updatedTime(String updatedTime) {
    this.updatedTime = updatedTime;
    return this;
  }

  
  @ApiModelProperty(example = "2024-08-02T12:00:00Z", value = "Timestamp when the governance policy was last updated.")
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
    GovernancePolicyDTO governancePolicy = (GovernancePolicyDTO) o;
    return Objects.equals(id, governancePolicy.id) &&
        Objects.equals(name, governancePolicy.name) &&
        Objects.equals(description, governancePolicy.description) &&
        Objects.equals(applicableArtifactTypes, governancePolicy.applicableArtifactTypes) &&
        Objects.equals(applicableStates, governancePolicy.applicableStates) &&
        Objects.equals(actionOnViolation, governancePolicy.actionOnViolation) &&
        Objects.equals(rules, governancePolicy.rules) &&
        Objects.equals(labels, governancePolicy.labels) &&
        Objects.equals(createdBy, governancePolicy.createdBy) &&
        Objects.equals(createdTime, governancePolicy.createdTime) &&
        Objects.equals(updatedBy, governancePolicy.updatedBy) &&
        Objects.equals(updatedTime, governancePolicy.updatedTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, applicableArtifactTypes, applicableStates, actionOnViolation, rules, labels, createdBy, createdTime, updatedBy, updatedTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GovernancePolicyDTO {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    applicableArtifactTypes: ").append(toIndentedString(applicableArtifactTypes)).append("\n");
    sb.append("    applicableStates: ").append(toIndentedString(applicableStates)).append("\n");
    sb.append("    actionOnViolation: ").append(toIndentedString(actionOnViolation)).append("\n");
    sb.append("    rules: ").append(toIndentedString(rules)).append("\n");
    sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
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

