package com.czar.service;

import com.czar.bean.Response;
import com.czar.bean.deposits.request.PostbackBean;
import com.czar.controller.WithdrawalController;
import com.czar.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommonCashierService implements ICommonCashierService {
    private static final Logger logger = LogManager.getLogger(WithdrawalController.class);
    private static final String paymentType1 = "UPI";
    private static final String paymentType2 = "phone_pe";
    @Autowired
    TransactionRepository transactionRepository;

    @Value("${andromeda.depositCallBack}")
    private String callback;


    public Response depositPostBack(PostbackBean postresponse) {

        if (postresponse != null) {
            String shortId = postresponse.transactions.get(0).getCustom_id();
            String amount = postresponse.transactions.get(0).getAmount();
            String txId = postresponse.transactions.get(0).getTransaction_id();
            String walletType = postresponse.transactions.get(0).getWallet_type();
            String data = transactionRepository.postreceipt(shortId, txId, walletType);
            logger.info("Postback received " + data);
            String acknowledgeTheRecord = transactionRepository.acknowledge(shortId);
            logger.info("acknowledgeTheRecord : --------->" + acknowledgeTheRecord);
            JSONObject jsonObject = new JSONObject(acknowledgeTheRecord);

            logger.info("data object from json: " + jsonObject);
            String domain = jsonObject.getString("domain");

            if (jsonObject.get("status").equals("success")) {
                Map<String, String> map = null;
                String status = "ok";
                Map<String, String> map1 = new HashMap<>();
                map1.put("amount", amount);
                map1.put("transaction_id", txId);
                map1.put("status", "success");
                map1.put("sid", shortId);
                URL url = null;
                String dCURL = null;
                if(domain.equalsIgnoreCase("rrrcasino")) {
                    dCURL = "https://rrrcasino.com/api/cashier/depositCallBack";
                }else {
                    dCURL = "https://777elite.com/api/cashier/depositCallBack";
                }
                try {
                    url = new URL(dCURL);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                String postData = "{ \"sid\":\"" + shortId + "\",\"amount\":\"" + amount + "\",\"status\":\"" + status + "\",\"txid\":\"" + txId + "\"}";
                logger.info("Deposit call back payload : " + postData);

                URLConnection conn = null;
                try {
                    conn = url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Content-Length", Integer.toString(postData.length()));

                try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                    dos.writeBytes(postData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String line;
                    while ((line = bf.readLine()) != null) {
                        System.out.println(line);
                        logger.info("Andromeda callback response ->" + line);

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new Response("ok");
            }else {
                return new Response("failed");
            }
        }
        return null;
    }

    @Override
    public String getPaymentMethods() {
        return transactionRepository.getpaymentmethods();
    }

    @Override
    public String postBackHandler(String payload) {
        return transactionRepository.postBackHandler(payload);
    }

    @Override
    public String getAcknowledgeRecord(String seque_no) {
        return transactionRepository.acknowledge(seque_no);
    }

    @Override
    public String getPostReceipt(String seno, String pShortId, String paymentMethod) {
        String t = transactionRepository.getreceipt(seno, pShortId, paymentMethod);

        return null;
    }

    @Override
    public String generatesequencenumber(String jsonObject) {

        return transactionRepository.generatesequencenumber(jsonObject);
    }

    @Override
    public String geturlbyseqnoandstate(String seq, String wsession) {
        return transactionRepository.findbyseqnoandstate(seq, wsession);
    }

    @Override
    public String withdrawStart(String json) {
        return transactionRepository.startWithdraw(json);
    }

    @Override
    public String postWithdraw(String sequeNo, String transactionId, String paymentMethod, String status) {
        return transactionRepository.postWithdraw(sequeNo, transactionId, paymentMethod, status);
    }

}
