package com.czar.service;

import com.czar.bean.Response;
import com.czar.bean.deposits.request.PostbackBean;
import org.springframework.data.repository.query.Param;

public interface ICommonCashierService {
  Response depositPostBack(PostbackBean postresponse);
  String getPaymentMethods();
  String generatesequencenumber(String jsonobject);
  String geturlbyseqnoandstate(String seq,String wsession);
  String postBackHandler(String payload);
  String snp_depositstatusenquiry(String seque_no,String txn_id);

  String getAcknowledgeRecord(String seque_no);
  String getPostReceipt(String seno,String pshortid,String paymentMethod);
   String withdrawStart(String json);
  String postWithdraw(String sequeNo,String transactionId,String paymentMethod,String status);
  Boolean Deposit_handler(@Param("sequeno") String sequeno , @Param("reqLoad") String reqLoad , @Param("respLoad") String respLoad);
}
