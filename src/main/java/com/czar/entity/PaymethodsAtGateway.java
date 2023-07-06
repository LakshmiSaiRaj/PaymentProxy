package com.czar.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name="tbl_gatewaySupportedPaymentmethods")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymethodsAtGateway {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "gateway")
    private String gateway;
    @Column(name = "payment_method")
    private String payment_method;

    @Column(name = "is_active")
    private Boolean is_active;

    @Column(name = "last_tran_tmsp")
    private Timestamp last_tran_tmsp;
    @Column(name = "req_method")
    private String req_method;

    @Column(name = "req_url")
    private String req_url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public Timestamp getLast_tran_tmsp() {
        return last_tran_tmsp;
    }

    public void setLast_tran_tmsp(Timestamp last_tran_tmsp) {
        this.last_tran_tmsp = last_tran_tmsp;
    }

    public String getReq_method() {
        return req_method;
    }

    public void setReq_method(String req_method) {
        this.req_method = req_method;
    }

    public String getReq_url() {
        return req_url;
    }

    public void setReq_url(String req_url) {
        this.req_url = req_url;
    }

    @Override
    public String toString() {
        return "PaymethodsAtGateway {" +
                "id=" + id +
                ", gateway='" + gateway + '\'' +
                ", payment_method='" + payment_method + '\'' +
                ", is_active=" + is_active +
                ", last_tran_tmsp=" + last_tran_tmsp +
                ", req_method='" + req_method + '\'' +
                ", req_url='" + req_url + '\'' +
                '}';
    }
}
