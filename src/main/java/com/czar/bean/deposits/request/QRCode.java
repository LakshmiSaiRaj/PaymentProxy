package com.czar.bean.deposits.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QRCode {

    @JsonProperty("PAY_ID")
    private String pay_Id;
    @JsonProperty("AMOUNT")
    private String amount;
    @JsonProperty("ORDER_ID")
    private String order_id;
    @JsonProperty("HASH")
    private String hash;


}
