package com.czar.controller;


import com.czar.service.ICommonCashierService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequestMapping("/deposit")
public class DepositController {
    private static final Logger logger = LogManager.getLogger(DepositController.class);
    @Autowired
    ICommonCashierService cashierService;

    @GetMapping(value = "/redirect", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public RedirectView redirect1(@RequestParam Map<String, String> map) {
        String s = null;

        logger.info("Reached /redirect URL with params : " + map);
        JSONObject jsonDbRes = null;
        JSONObject jdata = null;
        Boolean page = null;
        Boolean api = null;
        Boolean success = null;
        String url = null;

        RedirectView redirectView = new RedirectView();
        if (map != null) {
            logger.info("findBySeqNoAndstate() function i/p parameters " + map);
            s = cashierService.geturlbyseqnoandstate(String.valueOf(map.get("seque_no")), String.valueOf(map.get("wsession")));
            logger.info("DB response data : " + s);
            try {
                jsonDbRes = new JSONObject(s);

                if (jsonDbRes.get("status").equals("success") || !jsonDbRes.toString().isEmpty()) {
                    jdata = (JSONObject) jsonDbRes.get("data");
                    logger.info("DB RESPONSE JSON   : --> " + jsonDbRes);
                    logger.info("DATA JSON          : --> " + jdata);
                    success = true;
                } else {
                    success = false;
                    logger.info("status false");
                    redirectView.setUrl("error2");
                    return redirectView;
                }

//        logger.info( " Outside DATA : " + jsonDbRes.get("redirect_url") ) ;

                if (jdata.has("redirect_url")) {
                    url = (String) jdata.get("redirect_url");
                    logger.info("Redirect to url Page : " + url + " with request method " + jdata.get("httpmethod"));
                    redirectView.setUrl(url);
                    return redirectView;
                } else {
                    url = (String) jsonDbRes.get("redirect_url");
                    logger.info("OTHER Redirect to url : " + url + " request method " + jsonDbRes.get("httpmethod").toString());
                    String Authorization = jsonDbRes.get("Authorization").toString();
                    String Signature = null;
                    try {
                        Signature = hashPayload(jdata.toString(), jsonDbRes.get("signature").toString());
                    } catch (NoSuchAlgorithmException e) {
                        success = false;
                        logger.info("status false");
                        redirectView.setUrl("error2");
                        return redirectView;
                    } catch (InvalidKeyException e) {
                        success = false;
                        logger.info("status false");
                        redirectView.setUrl("error2");
                        return redirectView;
                    }
                    RestTemplate restTemplate = new RestTemplate();
                    org.springframework.http.HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("Authorization", "Basic " + Authorization);
                    headers.add("signature", Signature);
                    logger.info("Headers : " + headers);

                    org.springframework.http.HttpEntity<String> request =
                            new org.springframework.http.HttpEntity<String>(jdata.toString(), headers);

                    logger.info("Request : " + request);
                    String res = restTemplate.postForObject(url, request, String.class);
                    logger.info("Response from Partner : " + res);

                    JSONObject jsonRes = null;
                    try {
                        jsonRes = new JSONObject(res);
                        url = jsonRes.getString("redirect_url");
                        redirectView.setUrl("url");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                logger.info(" redirection Reached here for URL " + url);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Redirect View TO ===> " + url);
        redirectView.setUrl(url);
        return redirectView;
    }

    @GetMapping(value = "/payUrl", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String payUrl(@RequestParam Map<String, String> map) throws IOException {
        String responseURL = null;
        String encData = null;
        String payId = null;
        String responseData = null;
        logger.info("Letz PayUrl parameters : " + map);

        if (map != null) {
            logger.info("findBySeqNoAndstate() function i/p parameters " + map);
            responseURL = cashierService.geturlbyseqnoandstate(String.valueOf(map.get("seque_no")), String.valueOf(map.get("wsession")));
            logger.info("DB response data : " + responseURL);
            String imageData = null;
            try {
                JSONObject jsonObject = new JSONObject(responseURL);
                JSONObject data = null;
                if (jsonObject.get("status").equals("success")) {
                    responseURL = String.valueOf(jsonObject.get("data"));
                    jsonObject = (JSONObject) jsonObject.get("data");
                    logger.info("Redirect request ->" + responseURL + "URL :" + jsonObject);
                    data = new JSONObject(String.valueOf(jsonObject));
                } else {
                    logger.info("status false");
                }
                String payMethod = data.getString("payMethod");
                String params = String.valueOf(jsonObject.get("params"));
                jsonObject = (JSONObject) jsonObject.get("params");
                data = new JSONObject(String.valueOf(jsonObject));
                if (payMethod.equalsIgnoreCase("UPI")) {
                    encData = String.valueOf(data.getString("ENCDATA"));
                    payId = String.valueOf(data.getString("PAY_ID"));
                    logger.info("jsonObject  data : " + encData + " , payId : " + payId);
                    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                        HttpPost post = new HttpPost("https://secure.tpaysol.com/pgui/jsp/hostedpaymentrequest");
                        List<BasicNameValuePair> urlParameters = new ArrayList<>();
                        urlParameters.add(new BasicNameValuePair("ENCDATA", encData));
                        urlParameters.add(new BasicNameValuePair("PAY_ID", payId));
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(urlParameters, Consts.UTF_8);
                        post.setEntity(entity);

                        ResponseHandler<String> responseHandler = response -> {
                            int status = response.getStatusLine().getStatusCode();
                            if (status >= 200 && status < 300) {
                                HttpEntity responseEntity = response.getEntity();
                                return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                            } else {
                                throw new ClientProtocolException("Unexpected response status: " + status);
                            }
                        };
                        String responseBody = httpclient.execute(post, responseHandler);
                        System.out.println("----------------------------------------");
                        System.out.println(responseBody);
                        responseData = responseBody;
                    }

                } else {
                    logger.info("QRCODE Response : " + data);
                    logger.info("---------------> QRCODE Response 1:<------------------ " + data);
                    RestTemplate restTemplate = new RestTemplate();
                    try {

                        String qrURL = "https://secure.tpaysol.com/pgws/upiQRProcessor";
                        URL urlObj = new URL(qrURL);
                        HttpURLConnection postCon = (HttpURLConnection) urlObj.openConnection();
                        postCon.setRequestMethod("POST");
                        postCon.setRequestProperty("User-Agent", "Mozilla/5.0");
                        postCon.setRequestProperty("Content-Type", "application/json");
                        postCon.setDoOutput(true);
                        OutputStream osObj = postCon.getOutputStream();
                        osObj.write(params.getBytes());
                        osObj.flush();
                        osObj.close();
                        int respCode = postCon.getResponseCode();
                        System.out.println("Response from the server is: \n");
                        System.out.println("The POST Request Response Code :  " + respCode);
                        System.out.println("The POST Request Response Message : " + postCon.getResponseMessage());
                        StringBuffer sb = new StringBuffer();
                        if (respCode == HttpURLConnection.HTTP_OK) {
                            InputStreamReader irObj = new InputStreamReader(postCon.getInputStream());
                            BufferedReader br = new BufferedReader(irObj);
                            String input = null;
                            while ((input = br.readLine()) != null) {
                                sb.append(input);
                            }
                            logger.info("sb respoonse : " + sb);
                            br.close();
                            postCon.disconnect();
                            System.out.println(sb.toString());
                        } else {
                            System.out.println("POST Request did not work. Connection did not happen");
                        }

                        responseData = String.valueOf(sb);
                        logger.info("String response : " + responseData);
                        jsonObject = new JSONObject(responseData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);

                    }
                    String qrData = jsonObject.getString("UPI_QR_CODE");
                    String txnId = jsonObject.getString("TXN_ID");
                    String orderId = jsonObject.getString("ORDER_ID");
                    int amount = Integer.parseInt(jsonObject.getString("AMOUNT"));
                    int amountData = amount / 100;

                    logger.info("BASE64 Data : " + qrData);
                    jsonObject.remove("UPI_QR_CODE");
                    String responseDataOfQRCode = cashierService.postBackHandler(String.valueOf(jsonObject));
                    logger.info("db response from postBackHandler : " + responseDataOfQRCode);
                    logger.info("BASE64 Data : " + qrData);
                    imageData = qrData;
                    Model model = null;

                    responseData = "<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "  <meta charset=\\\"UTF-8\\\">\n" +
                            "  <title>Response Data</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<div>\n" +
                            "  <div>TransactionId : " + txnId + "</div>\n" +
                            "  <img src=\"data:image/jpeg;base64,\n" + imageData + '"' + "/>\n" +
                            "</div>\n" +
                            "</body>\n" +
                            "</html>\n";
                }

            } catch (JSONException e) {
                logger.error("Json Exception : " + e.getCause());
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                logger.error("Unsupported Encoding exception : " + e.getCause());
                throw new RuntimeException(e);
            } catch (ClientProtocolException e) {
                logger.error("cause of exception : " + e.getCause());
                throw new RuntimeException(e);
            } catch (IOException e) {
                logger.error("cause of exception : " + e.getCause());
                throw new RuntimeException(e);
            }
        }
        return responseData;

    }

    @RequestMapping(value = "/transactionStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TreeNode depositReceipt(@RequestBody Map<String, String> map) {
        logger.info("Transaction payload :  " + map);
        String rec = cashierService.snp_depositstatusenquiry(map.get("seque_no"), map.get("txnId"));
        logger.info("transactionStatus db response payload : " + rec);
        JsonNode json;
        try {
            json = new ObjectMapper().readTree(rec);
            logger.info("transactionStatus db response : " + json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }


    @RequestMapping(value = "/processedTransactionId", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TreeNode processedTransactionId(@RequestBody String payload) throws JsonProcessingException {
        Map<String, String> withdrawReq = new ObjectMapper().readValue(payload, Map.class);
        String rec = cashierService.postBackHandler(withdrawReq.get("txId"));
        JsonNode json;
        try {
            json = new ObjectMapper().readTree(rec);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    @GetMapping("/success")
    public String depositSuccess(String payLoad) {
        logger.info("Received payLoad  -> " + payLoad);
        return "success2";
    }


    @GetMapping("/pending")
    public String depositPending(String payLoad) {
        logger.info("Redirect url request ->  https://fairpays.org/deposit/pending");
        return "pending2";
    }

    @GetMapping("status/pending")
    public String statusPending(String payLoad) {
        logger.info("Redirect url request ->  https://fairpays.org/deposit/pending");
        return "pending2";
    }

    @GetMapping("/error")
    public String depositError(String payLoad) {
        logger.info("Redirect url request ->  https://fairpays.org/deposit/error");
        return "error2";
    }

    @GetMapping("/failure")
    public String depositFailure(String payLoad) {
        logger.info("Reached Redirect url request ->  https://fairpays.org/deposit/failure");
        return "failure2";
    }


    public String hashPayload(String payloadJson, String apiKey) throws NoSuchAlgorithmException, InvalidKeyException {
        logger.info("Reached hashPayload : " + payloadJson + " APIKEY : " + apiKey);
        SecretKeySpec secretKey = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(payloadJson.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            hashString.append(String.format("%02x", b & 0xff));
        }
        logger.info("Sending Response from hashPayload " + hashString.toString());
        return hashString.toString();
    }

}
