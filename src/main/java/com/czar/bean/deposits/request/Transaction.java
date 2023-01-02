package com.czar.bean.deposits.request;

import lombok.Data;

@Data
public class Transaction {
  public String amount;
  public String currency_code;
  public String wallet_type;
  public String transaction_id;
  public int transaction_type;
  public Object from;
  public String created_datetime;
  public String activated_datetime;
  public String custom_id;

}
