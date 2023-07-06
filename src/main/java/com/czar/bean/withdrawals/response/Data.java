package com.czar.bean.withdrawals.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class Data {
  public String user_id;
  public String redirectURL;
  public String reqMethod;
}
