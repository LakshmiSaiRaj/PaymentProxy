package com.czar.bean;

import com.czar.bean.userpayments.DomainBalance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private String transactionType;
  private String domain;
  private String paymentMethod;
  private String reqSend;
  private String reqRecieved;
  private String resSend;
  private String resRecieved;
  private String userId;
  private boolean reqStatus;
  private boolean resStatus;
  private String sequeNo;
  private long bal_bef;
  private long bal_aft;
  private String trnx_amount;
  private Timestamp startDate;
  private Timestamp endDate;
  private String state;
  private int hitCount;
  @OneToOne( cascade = CascadeType.ALL)
  // @PrimaryKeyJoinColumn
  private DomainBalance domainBalance;
  public Transactions(Transactions trnx) {
    this.id = trnx.id;
    this.transactionType = trnx.transactionType;
    this.domain = trnx.domain;
    this.paymentMethod = trnx.paymentMethod;
    this.reqSend = trnx.reqSend;
    this.reqRecieved = trnx.reqRecieved;
    this.resSend = trnx.resSend;
    this.resRecieved = trnx.resRecieved;
    this.userId = trnx.userId;
    this.reqStatus = trnx.reqStatus;
    this.resStatus = trnx.resStatus;
    this.sequeNo = trnx.sequeNo;
    this.bal_bef = trnx.bal_bef;
    this.bal_aft = trnx.bal_aft;
    this.trnx_amount = trnx.trnx_amount;
    this.startDate = trnx.startDate;
    this.endDate = trnx.endDate;
    this.state = trnx.state;
    this.hitCount = trnx.hitCount;
    this.domainBalance = trnx.domainBalance;
  }

}
