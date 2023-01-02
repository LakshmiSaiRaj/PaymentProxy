package com.czar.servlets;

import com.czar.service.CommonCashierService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Map;


@WebServlet(urlPatterns = "/trazPay/postDepositStatus", loadOnStartup = 1)

public class TranzPayDepositStatusInquiry extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Autowired
    CommonCashierService proxyService;
    private static final Logger logger = LogManager.getLogger(TranzPayDepositStatusInquiry.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response ) throws IOException {
        logger.info("URL : /tranzPay/postDepositStatus Servlet doPost  ==============>  " + request);
        logger.info("Content Type : ==============>  " + request.getContentType());
        String status = null;
        String domain = null;
        String sequeNumber = null;
        String amount = null;
        String trxId = null;
        JSONObject jsonObject = null;
        Model model= null;
        if(request.getContentType().equalsIgnoreCase("application/x-www-form-urlencoded")) {
            Map<String, String[]> requestParamMap = request.getParameterMap();
            String requestParameters = requestParamMap.toString();
            Enumeration parmaNames = request.getAttributeNames();
            Enumeration paramNames = request.getParameterNames();
            String paramNames1 = request.getParameterNames().toString();
            logger.info("parameter Names : " + paramNames + "paramNames2  : " + parmaNames + "paramNames3  : " + paramNames1 + "paramNames4  : " + requestParameters);
            jsonObject = new JSONObject();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String parameterValue = request.getParameterValues(paramName)[0];
                logger.info("paramName ==>  " + paramName + " value :" + parameterValue);
                jsonObject.put(paramName, parameterValue);
            }
            logger.info("jsonObject : " + jsonObject);
            String responseParams = proxyService.postBackHandler(String.valueOf(jsonObject));
            logger.info("response params : " + responseParams + "toString : " + responseParams);
            jsonObject = new JSONObject(responseParams);
            sequeNumber = String.valueOf(jsonObject.get("seque_no"));
            amount = String.valueOf(jsonObject.get("amount"));
            status = String.valueOf(jsonObject.get("status"));
            trxId = String.valueOf(jsonObject.get("trxn_id"));
            model.addAttribute("trxn_id",trxId);

            String acknowledgePaymentDomain = proxyService.getAcknowledgeRecord(sequeNumber);
            jsonObject = new JSONObject(acknowledgePaymentDomain);
            domain = jsonObject.getString("domain");
            logger.info("json object : " + jsonObject);
        }else{
            StringBuffer jb = new StringBuffer();
            String line = null;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Caues of error  : "+ e.getCause());
                /*report an error*/ }

            try {
                jsonObject =  new JSONObject(jb.toString());
                logger.info("QRCODE Response Data : " + jsonObject);

            } catch (JSONException e) {
                // crash and burn
                throw new IOException("Error parsing JSON request string");
            }
            logger.info("----------- > QRCode jsonObject : < ------------" + jsonObject);
            String responseParams = proxyService.postBackHandler(String.valueOf(jsonObject));
            logger.info("response params : ------------- >" + responseParams + "toString : " + responseParams);
            jsonObject = new JSONObject(responseParams);
            sequeNumber = String.valueOf(jsonObject.get("seque_no"));
            amount = String.valueOf(jsonObject.get("amount"));
            status = String.valueOf(jsonObject.get("status"));
            trxId = String.valueOf(jsonObject.get("trxn_id"));
            String acknowledgePaymentDomain = proxyService.getAcknowledgeRecord(sequeNumber);
            jsonObject = new JSONObject(acknowledgePaymentDomain);
            domain = jsonObject.getString("domain");
            logger.info("json object : " + jsonObject);

        }

        if (status.equals("Success")) {

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
            String postData = "{ \"sid\":\"" + sequeNumber + "\",\"amount\":\""
                    + amount + "\",\"status\":\"" + status + "\",\"txid\":\"" + trxId + "\"}";
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
            }
            response.sendRedirect("https://fairpays.org/deposit/success");
        } else {
            response.sendRedirect("https://fairpays.org/deposit/error");
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        logger.info("URL : /redirect Servlet doGet  ==============>  " + request +
                "URL : /response data : ");
        response.sendRedirect("https:///www.google.com");
    }
}
