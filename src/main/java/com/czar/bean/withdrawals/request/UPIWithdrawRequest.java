package com.czar.bean.withdrawals.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class UPIWithdrawRequest {
  private int amount;
  private String account_name;
  private String account_number;
  private String payment_system;
  private String signature;
  private String withdrawal_id;
  private String comment;
  private String user_id;
  private String type;
  private String is_test;
  private String wsession;
  private String pshortId;
  private String label;
  private String currency_code;
  private String payMethod;
  private String username;
/*
  user_id",
  type", "w
  wsession"
  pshortId"
  payMethod
  username"*/
}
