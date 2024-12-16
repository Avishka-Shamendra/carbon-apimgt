package org.wso2.carbon.apimgt.governance.rest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;



public class RuleContentDTO   {
  
    private String function = null;
    private Map<String, Object> parameters = new HashMap<String, Object>();

  /**
   * Name of the function.
   **/
  public RuleContentDTO function(String function) {
    this.function = function;
    return this;
  }

  
  @ApiModelProperty(example = "lengthInBetween", value = "Name of the function.")
  @JsonProperty("function")
  public String getFunction() {
    return function;
  }
  public void setFunction(String function) {
    this.function = function;
  }

  /**
   * Key-value parameters for the function.
   **/
  public RuleContentDTO parameters(Map<String, Object> parameters) {
    this.parameters = parameters;
    return this;
  }

  
  @ApiModelProperty(example = "{\"min\":10,\"max\":20}", value = "Key-value parameters for the function.")
  @JsonProperty("parameters")
  public Map<String, Object> getParameters() {
    return parameters;
  }
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleContentDTO ruleContent = (RuleContentDTO) o;
    return Objects.equals(function, ruleContent.function) &&
        Objects.equals(parameters, ruleContent.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(function, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuleContentDTO {\n");
    
    sb.append("    function: ").append(toIndentedString(function)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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

