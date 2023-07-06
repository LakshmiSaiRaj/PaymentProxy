package com.czar.bean.deposits.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@lombok.Data

public class Data {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String user_id;
  public String redirectURL;
  public String reqMethod;

}
