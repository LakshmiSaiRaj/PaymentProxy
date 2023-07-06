package com.czar.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "trans")
public class Trans {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "balance_after")
    private Long balanceAfter;

    @Column(name = "balance_before")
    private Long balanceBefore;

    @Column(name = "domain")
    private String domain;

    @Column(name = "end_date")
    private Timestamp endDate;
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "req_received")
    private String reqReceived;

    @Column(name = "req_send")
    private String reqSend;

    @Column(name = "req_status")
    private Boolean reqStatus;

    @Column(name = "res_received")
    private String resReceived;

    @Column(name = "res_status")
    private Boolean resStatus;

    @Column(name = "seque_no")
    private String sequeNo;


    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "state")
    private  String state;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "gateway")
    private String gateway;

    @Column(name = "hit_count")
    private Integer hitCount;

    @Column(name = "wsession")
    private String wsession;

    @Column(name = "login_name")
    private String loginName;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "trnx_amount")
    private Long trnxAmount;

    @Column(name = "last_func")
    private String lastFunc;

    @Column(name = "res_send")
    private Byte[] resSend;

    @Column(name = "postback_req")
    private String postbackReq;

    @Column(name = "postback_res")
    private String postbackRes;

    @Column(name = "trasaction_id")
    private String trasactionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Long balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Long getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(Long balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReqReceived() {
        return reqReceived;
    }

    public void setReqReceived(String reqReceived) {
        this.reqReceived = reqReceived;
    }

    public String getReqSend() {
        return reqSend;
    }

    public void setReqSend(String reqSend) {
        this.reqSend = reqSend;
    }

    public Boolean getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(Boolean reqStatus) {
        this.reqStatus = reqStatus;
    }

    public String getResReceived() {
        return resReceived;
    }

    public void setResReceived(String resReceived) {
        this.resReceived = resReceived;
    }

    public Boolean getResStatus() {
        return resStatus;
    }

    public void setResStatus(Boolean resStatus) {
        this.resStatus = resStatus;
    }

    public String getSequeNo() {
        return sequeNo;
    }

    public void setSequeNo(String sequeNo) {
        this.sequeNo = sequeNo;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }

    public String getWsession() {
        return wsession;
    }

    public void setWsession(String wsession) {
        this.wsession = wsession;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTrnxAmount() {
        return trnxAmount;
    }

    public void setTrnxAmount(Long trnxAmount) {
        this.trnxAmount = trnxAmount;
    }

    public String getLastFunc() {
        return lastFunc;
    }

    public void setLastFunc(String lastFunc) {
        this.lastFunc = lastFunc;
    }

    public Byte[] getResSend() {
        return resSend;
    }

    public void setResSend(Byte[] resSend) {
        this.resSend = resSend;
    }

    public String getPostbackReq() {
        return postbackReq;
    }

    public void setPostbackReq(String postbackReq) {
        this.postbackReq = postbackReq;
    }

    public String getPostbackRes() {
        return postbackRes;
    }

    public void setPostbackRes(String postbackRes) {
        this.postbackRes = postbackRes;
    }

    public String getTrasactionId() {
        return trasactionId;
    }

    public void setTrasactionId(String trasactionId) {
        this.trasactionId = trasactionId;
    }

    @Override
    public String toString() {
        return "Trans{" +
                "id=" + id +
                ", balanceAfter=" + balanceAfter +
                ", balanceBefore=" + balanceBefore +
                ", domain='" + domain + '\'' +
                ", EndDate=" + endDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", reqReceived='" + reqReceived + '\'' +
                ", reqSend='" + reqSend + '\'' +
                ", reqStatus=" + reqStatus +
                ", resReceived='" + resReceived + '\'' +
                ", resStatus=" + resStatus +
                ", sequeNo='" + sequeNo + '\'' +
                ", startDate=" + startDate +
                ", state='" + state + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", userId='" + userId + '\'' +
                ", gateway='" + gateway + '\'' +
                ", hitCount=" + hitCount +
                ", wsession='" + wsession + '\'' +
                ", loginName='" + loginName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", trnxAmount=" + trnxAmount +
                ", lastFunc='" + lastFunc + '\'' +
                ", resSend=" + resSend +
                ", postbackReq='" + postbackReq + '\'' +
                ", postbackRes='" + postbackRes + '\'' +
                ", trasactionId='" + trasactionId + '\'' +
                '}';
    }
}