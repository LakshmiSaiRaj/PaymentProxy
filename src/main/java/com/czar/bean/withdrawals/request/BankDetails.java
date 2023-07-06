package com.czar.bean.withdrawals.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankDetails {
  public String bank_code;

  public String getBank_code() {
    return bank_code;
  }

  public void setBank_code(String bank_code) {
    this.bank_code = bank_code;
  }
}
