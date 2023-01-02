package com.czar.repository;

import com.czar.bean.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {

  @Query(value = "select public.generatesequencenumber(?1)", nativeQuery = true)
  String generatesequencenumber(@Param("json") String json);

  @Query(value = "select public.findbyseqnoandstate(?1,?2)", nativeQuery = true)
  String findbyseqnoandstate(@Param("seqno") String seqno, @Param("wsession") String wsession);

  @Query(value = "select public.getpaymentmethods()", nativeQuery = true)
  String getpaymentmethods();

  @Query(value = "select public.postreceipt(?1,?2,?3)", nativeQuery = true)
  String postreceipt(@Param("seqno") String seqno, @Param("pshortid") String pshortid, @Param("walletType") String walletType);

  @Query(value = "select public.startwithdrawal(?1)", nativeQuery = true)
  String startWithdraw(@Param("json") String json);

  @Query(value = "select public.acknowledge(?1)", nativeQuery = true)
  String acknowledge(@Param("seqno") String seqno);

  @Query(value = "select public.postback_handler(?1)", nativeQuery = true)
  String postBackHandler(@Param("payload") String payload);

  @Query(value = "select public.postwithdrawal(?1,?2,?3,?4)", nativeQuery = true)
  String postWithdraw(@Param("sequeNo") String sequeNo, @Param("transactionId") String transactionId,
                      @Param("paymentMethod") String paymentMethod, @Param("status") String status);
  @Query(value = "select public.postreceipt(?1,?2,?3)", nativeQuery = true)
  String getreceipt(@Param("seqno") String seqno, @Param("pshortid") String pshortid,
                    @Param("paymentMethod")String paymentMethod);
}
