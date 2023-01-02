package com.czar.bean.withdrawals.response;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
  public class WithdrawResponse {
    public String status;
    public String msg;
    public Data data;
}
