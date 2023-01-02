package com.czar.service;

import com.czar.bean.Response;
import com.czar.bean.deposits.request.PostbackBean;

public interface ICommonCashierService {
  Response depositPostBack(PostbackBean postresponse);
  String getPaymentMethods();
  String generatesequencenumber(String jsonobject);
  String geturlbyseqnoandstate(String seq,String wsession);
  String postBackHandler(String payload);
  String getAcknowledgeRecord(String seque_no);
  String getPostReceipt(String seno,String pshortid,String paymentMethod);
   String withdrawStart(String json);
  String postWithdraw(String sequeNo,String transactionId,String paymentMethod,String status);
}
