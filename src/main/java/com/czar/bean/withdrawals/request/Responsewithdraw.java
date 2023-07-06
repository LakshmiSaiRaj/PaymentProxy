package com.czar.bean.withdrawals.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class Responsewithdraw {
    public String account_number;
    public int amount;
    public String payment_system;
    public String signature;
    public String withdrawal_id;
    public String comment;
    public String label;
    public boolean is_test;
    public String currency_code;
    public String response;

  }

