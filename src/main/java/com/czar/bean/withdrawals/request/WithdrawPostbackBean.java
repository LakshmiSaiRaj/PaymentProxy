package com.czar.bean.withdrawals.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class WithdrawPostbackBean {
    public String withdrawal_id;
    public String status;
    public String comment;
    public String payment_system;
    public String amount;
    public String currency_code;
    public String label;
    public String account_number;
    public String account_name;
    public String account_email;
    public PaymentsDetails payments_details;
    public BankDetails bank_details;
    public String signature;
  }


