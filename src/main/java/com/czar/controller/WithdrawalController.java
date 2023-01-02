package com.czar.controller;

import com.czar.bean.Response;
import com.czar.bean.deposits.request.PostbackBean;
import com.czar.bean.deposits.response.SequeResponse;
import com.czar.bean.withdrawals.request.*;
import com.czar.bean.withdrawals.response.WithdrawResponse;
import com.czar.service.CommonCashierService;
import com.czar.service.HttpClientHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/")
public class WithdrawalController {

    private static final Logger logger = LogManager.getLogger(WithdrawalController.class);
    @Value("${client.payKassmaWithdrawURL}")
    private String payment_url;
    @Value("${client.merchantId}")
    private String merchant_key;

    @Autowired
    CommonCashierService proxyService;

    @RequestMapping(value = "/sequenumberv1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SequeResponse generateSequenceNumber(@RequestBody String payLoad) {
        SequeResponse depositParams = null;
        try {
            logger.info("Request parameters generateSequenceNumber() " + payLoad);
            String res = proxyService.generatesequencenumber(payLoad);
            depositParams = new ObjectMapper().readValue(res, SequeResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        logger.info("Response generateSequenceNumber() " + depositParams);
        if (depositParams.getStatus().equalsIgnoreCase("success")) {

            depositParams.setStatus(depositParams.getStatus());
            depositParams.setMsg(depositParams.getMsg());
            depositParams.setData(depositParams.getData());
        } else {
            depositParams.setStatus("false");
            depositParams.setMsg("sequenceNumber getting null or something went wrong");
        }
        return depositParams;
    }

    @RequestMapping(value = "/postDepositStatus", method = RequestMethod.POST, consumes = MediaType.TEXT_HTML_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public String postDepositStatus(@RequestBody String payLoad) {
        logger.info("Request parameters postDepositStatus " + payLoad);
        String res = proxyService.postBackHandler(payLoad);

        return res;
    }

    @RequestMapping(value = "/getPaymentMethods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String paymentMethods() {
        logger.info("getPaymentMethods " + proxyService.getPaymentMethods());
        return proxyService.getPaymentMethods();
    }

    @RequestMapping(value = "/deposit/postback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response postback(@RequestBody String payLoad) throws JsonProcessingException {
        PostbackBean postBackResponse = null;
        Response response;

        postBackResponse = new ObjectMapper().readValue(payLoad, PostbackBean.class);
        response = proxyService.depositPostBack(postBackResponse);
        logger.info("postback response from db " + payLoad);

        return response;
    }


    @RequestMapping(value = "/withdraw/postback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String withdrawPostback(@RequestBody String payLoad) {
        WithdrawPostbackBean postbackresponse = null;
        logger.info(" post back payload : " + payLoad);
        String response = null;
        try {
            postbackresponse = new ObjectMapper().readValue(payLoad, WithdrawPostbackBean.class);
            String res = proxyService.postWithdraw(postbackresponse.getWithdrawal_id(), postbackresponse.getLabel(), postbackresponse.getPayment_system(), postbackresponse.getStatus());
            PostWithdraw postWithdraw = new ObjectMapper().readValue(res, PostWithdraw.class);
            if (postWithdraw.getStatus().equalsIgnoreCase("success")) {
                response = "{\"status\":\"ok\"}";
                logger.info("Beneficiary Added Successful " + response);
            }
            // response = proxyService.withdrawPostback(postbackresponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @RequestMapping(value = "/upiWithdrawalRequest",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String upiWithdrawalRequest(@RequestBody String payLoad) throws JsonProcessingException, NoSuchAlgorithmException {
        Map<String, String> withdrawReq = new ObjectMapper().readValue(payLoad, Map.class);
        logger.info("mapped withdraw request : " + withdrawReq);
        UPIWithdrawRequest payerDaTa = new ObjectMapper().readValue(payLoad, UPIWithdrawRequest.class);
        String trans = proxyService.withdrawStart(payLoad);
        logger.info("tans db response : " + trans);
        WithdrawResponse data = new ObjectMapper().readValue(trans, WithdrawResponse.class);
        if (data.status.equalsIgnoreCase("success")) {
            withdrawReq.put("withdrawal_id", String.valueOf(data.getData().getUser_id()));
            withdrawReq.remove("user_id");
            withdrawReq.remove("type");
//            is_test=true send 1 in signature
//            if is_test=false send null
            String md5String = withdrawReq.get("account_number") + ":" + payerDaTa.getAmount() + ":withdrawal:INR:" + ":" + withdrawReq.get("label") + ":"
                    + withdrawReq.get("payment_system") + ":" + String.valueOf(data.getData().getUser_id());
            logger.info("upiWithdrawalRequest MD5String : " + md5String);
            String sig = getSHA1(merchant_key + (getMd5(md5String)));

            withdrawReq.put("signature", sig);
        }
        withdrawReq.remove("wsession");
        withdrawReq.remove("payMethod");
        withdrawReq.remove("username");
        withdrawReq.remove("pshortId");
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(withdrawReq);
        JSONObject mJSONObject1;
        try {
            mJSONObject1 = new JSONObject(json1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        logger.info("Request from proxy" + withdrawReq);
        HttpRequest request = HttpRequest.newBuilder(URI.create(payment_url))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(mJSONObject1)))
                .build();
        logger.info("Beneficiary Added Successful " + mJSONObject1);

        HttpResponse<String> httpResponse1 = null;
        JSONObject json;

        String res;
        try {
            httpResponse1 = HttpClientHelper.send(request);

            String resStr = ((HttpResponse<?>) httpResponse1).body().toString();
            json = new JSONObject(resStr);
            json.put("tnx_details", resStr);
            logger.info("Response From PayKassma " + json);

            res = null;
            if (String.valueOf(json.get("status")).equalsIgnoreCase("success") ||
                    String.valueOf(json.get("status")).equalsIgnoreCase("ok")) {
                withdrawReq.put("response", String.valueOf(json));
                String resp = proxyService.postWithdraw(data.getData().getUser_id(), withdrawReq.get("label"),
                        withdrawReq.get("payment_system"), String.valueOf(json.get("status")));
                logger.info("if response : " + res);
                PostWithdraw postWithdraw = new ObjectMapper().readValue(resp, PostWithdraw.class);
            } else {
                withdrawReq.put("response", String.valueOf(json));
                String resp = proxyService.postWithdraw(data.getData().getUser_id(), withdrawReq.get("label"),
                        withdrawReq.get("payment_system"), String.valueOf(json.get("status")));
                logger.info("if response : " + res);
                PostWithdraw postWithdraw = new ObjectMapper().readValue(resp, PostWithdraw.class);
            }

        } catch (IOException | InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }
        return String.valueOf(json);
    }

    @RequestMapping(value = "/impsWithdrawalRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String impsWithdrawalRequest(@RequestBody String payLoad) throws JsonProcessingException, NoSuchAlgorithmException {
        Map<String, String> withdrawReq = new ObjectMapper().readValue(payLoad, Map.class);
        logger.info("paykassma impsWithdrawalRequest : " + withdrawReq + "payload : " + payLoad);
        String trans = proxyService.withdrawStart(payLoad);
        IMPSWithdrawRequest requestData = new ObjectMapper().readValue(payLoad, IMPSWithdrawRequest.class);
        WithdrawResponse data = new ObjectMapper().readValue(trans, WithdrawResponse.class);
        String sig = null;
        logger.info("impsWithdrawalRequest response data from proxy db : " + data);
        if (data.status.equalsIgnoreCase("success")) {
            withdrawReq.put("withdrawal_id", String.valueOf(data.getData().getUser_id()));
            withdrawReq.remove("user_id");
            withdrawReq.remove("type");
            //   is_test=true send 1 in signature
            //   if is_test=false send null
            String md5String = requestData.getAccount_name() + ":"
                    + requestData.getAccount_number() + ":" + requestData.getAmount() + ":" + requestData.getBank_details().getBank_code()
                    + ":" + requestData.getCurrency_code() + "::" + requestData.getLabel() + "::" + "imps_ib"
                    + ":" + data.getData().getUser_id();

            logger.info("signature data ::::  " + md5String);
            sig = getSHA1(merchant_key + (getMd5(md5String)));
            logger.info("signature value  : " + sig);

            logger.info("withdraw request before post : " + withdrawReq);
        }
        requestData.setSignature(sig);
        requestData.setPayment_system("imps_ib");
        requestData.setWithdrawal_id(data.getData().getUser_id());
        PaymentsDetails paymentsDetails = new PaymentsDetails();
        requestData.setPayment_details(paymentsDetails);
        logger.info(" request data : " + requestData);
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(requestData);
        logger.info("JSON Request :" + json1);
        JSONObject mJSONObject1;
        try {
            mJSONObject1 = new JSONObject(json1);

            logger.info("json response :" + mJSONObject1);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        mJSONObject1.remove("wsession");
        mJSONObject1.remove("payMethod");
        mJSONObject1.remove("type");
        mJSONObject1.remove("user_id");
        mJSONObject1.remove("comment");
        mJSONObject1.remove("pshortId");
        mJSONObject1.remove("username");
        logger.info("Request from proxy" + withdrawReq);
        HttpRequest request = HttpRequest.newBuilder(URI.create(payment_url))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(mJSONObject1))).build();
        logger.info("Beneficiary Added Successful " + mJSONObject1);

        HttpResponse<String> httpResponse1 = null;
        JSONObject json;
        String res;
        try {
            httpResponse1 = HttpClientHelper.send(request);

            String resStr = ((HttpResponse<?>) httpResponse1).body().toString();
            json = new JSONObject(resStr);
            json.put("tnx_details", resStr);
            logger.info("Response From Paykassma " + json);

            res = null;
            if (String.valueOf(json.get("status")).equalsIgnoreCase("success") ||
                    String.valueOf(json.get("status")).equalsIgnoreCase("ok")) {
                withdrawReq.put("response", String.valueOf(json));
                String response = proxyService.postWithdraw(data.getData().getUser_id(), requestData.getLabel(),
                        "imps_ib", String.valueOf(json.get("status")));
                logger.info("if response : " + res);
                PostWithdraw postWithdraw = new ObjectMapper().readValue(response, PostWithdraw.class);
                logger.info("if response post : " + postWithdraw);
            } else {
                String response = proxyService.postWithdraw(data.getData().getUser_id(), requestData.getLabel(),
                        "imps_ib", String.valueOf(json.get("status")));
                logger.info("if response : " + response);
                PostWithdraw postWithdraw = new ObjectMapper().readValue(response, PostWithdraw.class);
                logger.info("if response post : " + postWithdraw);
            }
        } catch (IOException | InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }
        return String.valueOf(json);

    }

    public String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSHA1(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] digest = sha1.digest((input).getBytes());
        StringBuilder string = new StringBuilder();
        for (byte b : digest) {
            String hexString = Integer.toHexString(0x00FF & b);
            string.append(hexString.length() == 1 ? "0" + hexString : hexString);
        }
        return string.toString();
    }
}
