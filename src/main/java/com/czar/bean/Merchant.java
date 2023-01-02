package com.czar.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class Merchant {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private double total_cur_balance;
 // private boolean deposit_status;
  private double deposit_sum;
  private String last_transaction;
  private double last_transaction_before_balance;
  private String last_transaction_id;
  private  String last_transaction_operator;
  private String last_transaction_pgw;
  private Timestamp last_transaction_timestamp;
  private double withdraw_sum;
  private double last_transaction_after_balance;
  private String redirect_url;
  private String deposit_count;
  private String withdraw_count;
  @JoinColumn(referencedColumnName = "id")
  private String transaction_id;
}
