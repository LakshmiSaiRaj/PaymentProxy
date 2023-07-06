package com.czar.repository;
import  com.czar.entity.PaymethodsAtGateway;
import com.czar.entity.Trans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymethodsAtGatewayRepository extends JpaRepository<PaymethodsAtGateway, Long> {

//    Page<PaymethodsAtGateway> findAll(Pageable pageable);
    @Query(value = "select * FROM public.gateway_supported_paymentmethods order by gateway, payment_method",nativeQuery = true)
    List<PaymethodsAtGateway> findAll();

}
