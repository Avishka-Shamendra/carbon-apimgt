package org.wso2.carbon.apimgt.governance.rest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.apimgt.governance.rest.api.dto.RuleInfoDTO;
import javax.validation.constraints.*;

/**
 * A list of rules.
 **/

import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

@ApiModel(description = "A list of rules.")

public class RuleListDTO   {
  
    private Integer count = null;
    private List<RuleInfoDTO> list = new ArrayList<RuleInfoDTO>();

  /**
   * Number of rules returned.
   **/
  public RuleListDTO count(Integer count) {
    this.count = count;
    return this;
  }

  
  @ApiModelProperty(example = "2", value = "Number of rules returned.")
  @JsonProperty("count")
  public Integer getCount() {
    return count;
  }
  public void setCount(Integer count) {
    this.count = count;
  }

  /**
   * List of rules.
   **/
  public RuleListDTO list(List<RuleInfoDTO> list) {
    this.list = list;
    return this;
  }

  
  @ApiModelProperty(value = "List of rules.")
      @Valid
  @JsonProperty("list")
  public List<RuleInfoDTO> getList() {
    return list;
  }
  public void setList(List<RuleInfoDTO> list) {
    this.list = list;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleListDTO ruleList = (RuleListDTO) o;
    return Objects.equals(count, ruleList.count) &&
        Objects.equals(list, ruleList.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(count, list);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RuleListDTO {\n");
    
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    list: ").append(toIndentedString(list)).append("\n");
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

