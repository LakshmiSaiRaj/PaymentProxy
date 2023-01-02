package com.czar.bean.deposits.request;

import lombok.Data;

import java.util.ArrayList;
@Data
public class PostbackBean {
  public String access_key;
  public String signature;
  public String label;
  public Stockpiling stockpiling;
  public int stockpiling_id;
  public ArrayList<Transaction> transactions;
}

