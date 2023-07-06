package com.czar.bean.withdrawals.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostWithdraw {
    public String status;
    public String message;
}
