package com.czar.bean.withdrawals.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Withdrawreq {
  public String withdrawal_id;
  public String payment_system;
  public int amount;
  public String currency_code;
  public String label;
  public boolean is_test;
  public String comment;
  public String account_number;
  public String signature;
}
