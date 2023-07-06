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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
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

    @RequestMapping(value = "/sequenumberv1",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)

    public String generateSequenceNumber(@RequestBody String payLoad) {
        SequeResponse depositParams = null;
        String res;

        logger.info("Request parameters generateSequenceNumber() " + payLoad);
        res = proxyService.generatesequencenumber(payLoad);
        logger.info("Response from generateSequenceNumber() " + res);
        JSONObject jsonres = new JSONObject(res);
        JSONObject jsondata = (JSONObject) jsonres.get("data");
        String reqMethod = jsondata.get("reqMethod").toString();
        logger.info("reqMethod : " + reqMethod);
        if (reqMethod.equalsIgnoreCase("PAGE")) {
            return res;
        } else if (reqMethod.equalsIgnoreCase("SAPI")) {
            jsonres = new JSONObject(res);
            jsondata = (JSONObject) jsonres.get("data");
            String redirectURL = jsondata.get("redirectURL").toString();
            String Authorization = jsondata.get("Authorization").toString();
            String user_token = jsondata.get("user_token").toString();
            String amount = jsondata.get("amount").toString();
            String order_id = jsondata.get("order_id").toString();
            String customer_name = jsondata.get("customer_name").toString();
            String customer_mobile = jsondata.get("customer_mobile").toString();
            logger.info("redirectURL : " + redirectURL);
            logger.info("Authorization : " + Authorization);
            logger.info("user_token : " + user_token);
            logger.info("amount : " + amount);
            logger.info("order_id : " + order_id);
            logger.info("customer_name : " + customer_name);
            logger.info("customer_mobile : " + customer_mobile);

            JSONObject reqjson = new JSONObject();
            reqjson.put("user_token", user_token);
            reqjson.put("amount", amount);
            reqjson.put("order_id", order_id);
            reqjson.put("customer_name", customer_name);
            reqjson.put("customer_mobile", customer_mobile);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Basic " + Authorization);
            logger.info("Headers : " + headers);
            HttpEntity<String> request =
                    new HttpEntity<String>(reqjson.toString(), headers);
            logger.info("Request : " + request);
            ResponseEntity<String> responseEntityStr = restTemplate.
                    postForEntity(redirectURL, request, String.class);
            logger.info("Response from Partner : " + responseEntityStr);
            res = responseEntityStr.getBody();
            Boolean status = proxyService.Deposit_handler(jsondata.get("user_id").toString(), request.toString(), res);
        }

        return res;

    }

    @RequestMapping(value = "/postDepositStatus",
            method = RequestMethod.POST,
            consumes = MediaType.TEXT_HTML_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public String postDepositStatus(@RequestBody String payLoad) {
        logger.info("Request parameters postDepositStatus " + payLoad);
        String res = proxyService.postBackHandler(payLoad);
        logger.info("DB Response : " + res);

        return res;
    }

    @RequestMapping(value = "/algopostback",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.ALL_VALUE)
    public String AlgoDepositStatus(@RequestBody String payLoad) {
        logger.info("Request parameters AlgoDepositStatusPostback " + payLoad);
        String res = proxyService.buxDepositcallback_handler(payLoad);
        logger.info("DB Response : " + res);
        JSONObject jsonRes = new JSONObject(res);
        if (jsonRes.getString("status").equalsIgnoreCase("success")) {
            String callbackURL = jsonRes.get("callbackURL").toString();
            jsonRes.remove("callbackURL");
            logger.info("JSON Payload  : " + jsonRes + "callbackURL" + callbackURL);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request =
                    new HttpEntity<String>(jsonRes.toString(), headers);
            logger.info("Request : " + request);
            ResponseEntity<String> responseEntityStr = restTemplate.
                    postForEntity(callbackURL, request, String.class);
            logger.info("Response from Partner : " + responseEntityStr);
            String resp = responseEntityStr.getBody();
            logger.info("DOMAIN POSTBACK RESPONSE  : " + resp);
        } else {
            logger.info("status failed" + jsonRes);
            return jsonRes.getString("message");
        }
        return "Success";
    }

    @RequestMapping(value = "/buxpostback",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.ALL_VALUE)
    public String DepositStatus(@RequestBody String payLoad) {
        logger.info("Request parameters postDepositStatus " + payLoad);
        String res = proxyService.buxDepositcallback_handler(payLoad);
        logger.info("DB Response : " + res);
        JSONObject jsonRes = new JSONObject(res);
        if (jsonRes.getString("status").equalsIgnoreCase("success")) {
            String callbackURL = jsonRes.get("callbackURL").toString();
            jsonRes.remove("callbackURL");
            logger.info("JSON Payload  : " + jsonRes + "callbackURL" + callbackURL);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request =
                    new HttpEntity<String>(jsonRes.toString(), headers);
            logger.info("Request : " + request);
            ResponseEntity<String> responseEntityStr = restTemplate.
                    postForEntity(callbackURL, request, String.class);
            logger.info("Response from Partner : " + responseEntityStr);
            String resp = responseEntityStr.getBody();
            logger.info("DOMAIN POSTBACK RESPONSE  : " + resp);
        } else {
            logger.info("status failed" + jsonRes);
            return jsonRes.getString("message");
        }
        return "Success";
    }

    @RequestMapping(value = "/getPaymentMethods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String paymentMethods() {
        logger.info("getPaymentMethods " + proxyService.getPaymentMethods());
        return proxyService.getPaymentMethods();
    }

    @RequestMapping(value = "/deposit/postback",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
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

    public String hashPayload(String payloadJson, String apiKey) throws NoSuchAlgorithmException, InvalidKeyException {
        logger.info( "Reached hashPayload : " + payloadJson + " APIKEY : " +  apiKey   );
        SecretKeySpec secretKey = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(payloadJson.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashString = new StringBuilder(2 * hash.length);
        for(byte b : hash) {
            hashString.append(String.format("%02x", b&0xff));
        }
        logger.info( "Sending Response from hashPayload " + hashString.toString());
        return hashString.toString();
    }
    @RequestMapping(value = "/impsWithdrawalRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String impsWithdrawalRequest(@RequestBody String payLoad) throws JsonProcessingException, NoSuchAlgorithmException {
        Map<String, String> withdrawReq = new ObjectMapper().readValue(payLoad, Map.class);
        logger.info("paykassma impsWithdrawalRequest : " + withdrawReq + '\n' + "payload : " + payLoad);
        String trans = proxyService.withdrawStart(payLoad);
        logger.info("DB Response in impsWithdrawalRequest : " + trans);
        JSONObject jsonDbRes = null;
        JSONObject jparams = null;
        String url = null;
        jsonDbRes = new JSONObject(trans);
        if (jsonDbRes.get("status").equals("success") && jsonDbRes.get("req_method").equals("SAPI")) {
            try {

                jparams = (JSONObject) jsonDbRes.get("params");
                logger.info("PARAMS JSON   : -->" + jparams);
                logger.info("DBRESJSON   : -->" + jsonDbRes);
                url = (String) jsonDbRes.get("redirect_url");
                String Authorization = jsonDbRes.get("Authorization").toString();
                String Signature = jsonDbRes.get("signature").toString();
                RestTemplate restTemplate = new RestTemplate();
                org.springframework.http.HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Basic " + Authorization);
                Signature     = null ;
                try {
                    Signature = hashPayload ( jparams.toString(),  jsonDbRes.get("signature").toString());
                }
                catch (NoSuchAlgorithmException | InvalidKeyException e )
                {
                    e.getCause();
                }
                headers.add("Signature",Signature);
                logger.info("Headers : " + headers);
                org.springframework.http.HttpEntity<String> request =
                        new org.springframework.http.HttpEntity<String>(jparams.toString(), headers);
                logger.info("Request : " + request);
                String res = restTemplate.postForObject(url, request, String.class);
                logger.info("Response from Partner : " + res);
                return "Status Awaiting";

            } catch (JSONException e) {

                throw new RuntimeException(e);

            }
        }

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
                logger.info("withdraw Request : " + withdrawReq);
                String md5String = requestData.getAccount_name() + ":"
                        + requestData.getAccount_number() + ":" + requestData.getAmount() + ":" + requestData.getBank_code()
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
                logger.info("Response From Partner " + json);

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
