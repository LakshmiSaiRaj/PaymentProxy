package com.czar.bean.withdrawals.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class IMPSWithdrawRequest {
  private String payment_system;
  private Integer amount;
  private String currency_code;
  private String label;
  private String signature;

  private BankDetails bank_details;
  private boolean is_test;

  //  private String bank_code;
  private String comment;
  private String account_number;
  private String account_name;
  private String user_id;
  private String type;
  private String wsession;
  private String pshortId;
  private PaymentsDetails payment_details;
  private String payMethod;
  private String username;
  private String withdrawal_id;


  // Getter Methods

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getWithdrawal_id() {
    return withdrawal_id;
  }

  public void setWithdrawal_id(String withdrawal_id) {
    this.withdrawal_id = withdrawal_id;
  }

  public String getPayment_system() {
    return payment_system;
  }

  public int getAmount() {
    return amount;
  }

  public String getCurrency_code() {
    return currency_code;
  }

  public String getLabel() {
    return label;
  }

  public boolean getIs_test() {
    return is_test;
  }

  public String getComment() {
    return comment;
  }

  public String getAccount_number() {
    return account_number;
  }

  public String getUser_id() {
    return user_id;
  }

  public String getType() {
    return type;
  }

  public String getWsession() {
    return wsession;
  }

  public String getPshortId() {
    return pshortId;
  }

  public String getPayMethod() {
    return payMethod;
  }

  public String getUsername() {
    return username;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
// Setter Methods

  public void setPayment_system(String payment_system) {
    this.payment_system = payment_system;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public void setCurrency_code(String currency_code) {
    this.currency_code = currency_code;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setIs_test(boolean is_test) {
    this.is_test = is_test;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setAccount_number(String account_number) {
    this.account_number = account_number;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setWsession(String wsession) {
    this.wsession = wsession;
  }

  public void setPshortId(String pshortId) {
    this.pshortId = pshortId;
  }

  public void setPayMethod(String payMethod) {
    this.payMethod = payMethod;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public BankDetails getBank_details() {
    return bank_details;
  }

  public void setBank_details(BankDetails bank_details) {
    this.bank_details = bank_details;
  }

  public boolean isIs_test() {
    return is_test;
  }

  public PaymentsDetails getPayment_details() {
    return payment_details;
  }

  public void setPayment_details(PaymentsDetails payment_details) {
    this.payment_details = payment_details;
  }

  /*  public String getBank_code() {
    return bank_code;
  }

  public void setBank_code(String bank_code) {
    this.bank_code = bank_code;
  }*/

  public String getAccount_name() {
    return account_name;
  }

  public void setAccount_name(String account_name) {
    this.account_name = account_name;
  }
}
