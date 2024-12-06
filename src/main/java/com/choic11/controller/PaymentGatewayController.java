package com.choic11.controller;

import com.choic11.GlobalConstant.PaymentGatewayConstant;
import com.choic11.Util;
import com.choic11.model.response.BaseResponse;
import com.choic11.paymentgateway.CASHFREE;
import com.choic11.service.CustomerService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/paymentcallback")
public class PaymentGatewayController extends BaseController {

    @Autowired
    CustomerService customerService;

    @PostMapping("payu_payment_webhook")
    public ResponseEntity<Object> payuPaymentWebhook(HttpServletRequest request) {


        String receivedJson = "";
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
            receivedJson = stringBuilder.toString().trim();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        JSONObject jsonFromPayuData = new JSONObject();
        if (!Util.isEmpty(receivedJson)) {
            try {
                jsonFromPayuData = new JSONObject(receivedJson);
            }catch (Exception e){
                e.printStackTrace();
                Util.printLog2("--------------------------------------",receivedJson);
            }
        }


        String jsonFromPayu = "";
        if (!jsonFromPayuData.isEmpty()) {
            jsonFromPayu = jsonFromPayuData.toString();
        }


        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("STATUS", "");
        output.put("RESPMSG", "Something went wrong.");
        try {
            if (jsonFromPayu != null && !jsonFromPayu.isEmpty()) {
                JSONObject jsonObject = new JSONObject(jsonFromPayu);

                String hashString = jsonObject.optString("udf10", "") + "|" + jsonObject.optString("udf9", "") + "|"
                        + jsonObject.optString("udf8", "") + "|" + jsonObject.optString("udf7", "") + "|" + jsonObject.optString("udf6", "") + "|"
                        + jsonObject.optString("udf5", "") + "|" + jsonObject.optString("udf4", "") + "|" + jsonObject.optString("udf3", "") + "|"
                        + jsonObject.optString("udf2", "") + "|" + jsonObject.optString("udf1", "") + "|" + jsonObject.get("customerEmail")
                        + "|" + jsonObject.get("customerName") + "|" + jsonObject.get("productInfo") + "|"
                        + jsonObject.get("amount") + "|" + jsonObject.get("merchantTransactionId");

                HashMap<String, String> payuPaymentDetail = PaymentGatewayConstant.getPayuPaymentDetail();

                hashString += "|" + payuPaymentDetail.get("PAYU_MERCHANT_KEY");

                hashString = payuPaymentDetail.get("PAYU_SALT") + "|" + jsonObject.optString("status").toLowerCase()
                        + "|" + hashString;


                String CalcHashString = Util.getSHA512(hashString);
                CalcHashString = CalcHashString.toLowerCase();

                String promocode = jsonObject.optString("udf2", "");

                jsonObject.put("referrer", jsonObject.get("udf1"));
                jsonObject.put("promocode", promocode);

                String customerId = jsonObject.get("udf3").toString();
                String status = jsonObject.get("status").toString().toLowerCase();
                String amount = jsonObject.get("amount").toString();
                String hash = jsonObject.get("hash").toString();
                String mode = jsonObject.optString("paymentMode", "");

                output.put("STATUS", status);

                String ORDER_ID = jsonObject.get("merchantTransactionId").toString();
                String logRefrenceId = jsonObject.get("paymentId").toString();
                String logStatus = jsonObject.get("status").toString().toLowerCase();
                String logPaymentGateway = "PAYU_WEBHOOK";
                String logRequestJson = new Gson().toJson(new HashMap<String, String>());
                String logResponseJson = jsonFromPayu;

                int customerIdInt = Integer.parseInt(customerId);
                float amountFloat = Float.parseFloat(amount);

                customerService.getCustomerRepository().insertPaymentLog(customerIdInt, ORDER_ID, logRefrenceId,
                        logStatus, amountFloat, logPaymentGateway, logRequestJson, logResponseJson);

                if (status.equals("success")) {

                    if (hash.equals(CalcHashString)) {

                        output.put("STATUS", "TXN_SUCCESS");
                        output.put("TXNAMOUNT", amount);

                        int checkTransactionIdAlreadyExist = customerService.getCustomerRepository()
                                .checkTransactionIdAlreadyExist(customerIdInt, ORDER_ID);

                        if (checkTransactionIdAlreadyExist == 0) {

                            customerService.updateCustomerWalletRecharge(customerIdInt, amountFloat, ORDER_ID,
                                    logResponseJson, "PAYU", mode, promocode);

                        }

                        LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerService
                                .getCustomerRepository().getUpdatedWalletData(customerIdInt);

                        output.put("wallet", updatedWalletData.get("wallet"));

                    } else {
                        output.put("RESPMSG", "hash no matched.");
                    }

                } else {
                    output.put("RESPMSG", status);
                }
            } else {
                output.put("RESPMSG", "Something went wrong.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            output.put("RESPMSG", e.getMessage());
        }

        if (output.get("STATUS").equals("TXN_SUCCESS")) {
            BaseResponse baseResponse = new BaseResponse(0, false,
                    "Wallet reharge successfully with amount ₹ " + output.get("TXNAMOUNT"), output);

            return echoRespose(baseResponse, HttpStatus.OK);

        } else {
            BaseResponse baseResponse = new BaseResponse(0, true, output.get("RESPMSG").toString(), output);

            return echoRespose(baseResponse, HttpStatus.OK);
        }

    }

    @PostMapping("payu_wallet_callback")
    public ResponseEntity<Object> payuWalletCallback(HttpServletRequest request) {

        JSONObject jsonFromPayuData = new JSONObject();
        Map<String, String[]> parameters = request.getParameterMap();
        Set<String> keySet = parameters.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            jsonFromPayuData.put(key, parameters.get(key)[0]);
        }

        String jsonFromPayu = jsonFromPayuData.toString();

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("STATUS", "");
        output.put("RESPMSG", "Something went wrong.");

        if (jsonFromPayu != null && !jsonFromPayu.isEmpty()) {
            JSONObject jsonObject = new JSONObject(jsonFromPayu);

            String payUMessage = jsonObject.optString("field9");

            String hashString = jsonObject.get("udf10") + "|" + jsonObject.get("udf9") + "|" + jsonObject.get("udf8")
                    + "|" + jsonObject.get("udf7") + "|" + jsonObject.get("udf6") + "|" + jsonObject.get("udf5") + "|"
                    + jsonObject.get("udf4") + "|" + jsonObject.get("udf3") + "|" + jsonObject.get("udf2") + "|"
                    + jsonObject.get("udf1") + "|" + jsonObject.get("email") + "|" + jsonObject.get("firstname") + "|"
                    + jsonObject.get("productinfo") + "|" + jsonObject.get("amount") + "|" + jsonObject.get("txnid")
                    + "|" + jsonObject.get("key");

            HashMap<String, String> payuPaymentDetail = PaymentGatewayConstant.getPayuPaymentDetail();

            hashString = payuPaymentDetail.get("PAYU_SALT") + "|" + jsonObject.get("status") + "|" + hashString;

            String CalcHashString = Util.getSHA512(hashString);
            CalcHashString = CalcHashString.toLowerCase();

            String promocode = jsonObject.optString("udf2", "");

            jsonObject.put("referrer", jsonObject.get("udf1"));
            jsonObject.put("promocode", promocode);

            String customerId = jsonObject.get("udf3").toString();
            String status = jsonObject.get("status").toString();
            String amount = jsonObject.get("amount").toString();
            String hash = jsonObject.get("hash").toString();
            String mode = jsonObject.optString("mode", "");

            output.put("STATUS", status);

            String ORDER_ID = jsonObject.get("txnid").toString();
            String logRefrenceId = jsonObject.get("txnid").toString();
            String logStatus = jsonObject.get("status").toString();
            String logPaymentGateway = "PAYU_CALLBACK";
            String logRequestJson = new Gson().toJson(new HashMap<String, String>());
            String logResponseJson = jsonFromPayu;

            int customerIdInt = Integer.parseInt(customerId);
            float amountFloat = Float.parseFloat(amount);

            customerService.getCustomerRepository().insertPaymentLog(customerIdInt, ORDER_ID, logRefrenceId, logStatus,
                    amountFloat, logPaymentGateway, logRequestJson, logResponseJson);

            if (status.equals("success")) {

                if (hash.equals(CalcHashString)) {

                    output.put("STATUS", "TXN_SUCCESS");
                    output.put("TXNAMOUNT", amount);

                    int checkTransactionIdAlreadyExist = customerService.getCustomerRepository()
                            .checkTransactionIdAlreadyExist(customerIdInt, logRefrenceId);

                    if (checkTransactionIdAlreadyExist == 0) {

                        customerService.updateCustomerWalletRecharge(customerIdInt, amountFloat, logRefrenceId,
                                logResponseJson, "PAYU", mode, promocode);

                    }

                    LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerService
                            .getCustomerRepository().getUpdatedWalletData(customerIdInt);

                    output.put("wallet", updatedWalletData.get("wallet"));
                } else {
                    output.put("RESPMSG", "hash no matched.");
                }

            } else {
                output.put("RESPMSG", payUMessage);
            }
        } else {
            output.put("RESPMSG", "Something went wrong.");
        }

        if (output.get("STATUS").equals("TXN_SUCCESS")) {
            BaseResponse baseResponse = new BaseResponse(0, false,
                    "Wallet reharge successfully with amount ₹ " + output.get("TXNAMOUNT"), output);

            return echoRespose(baseResponse, HttpStatus.OK);

        } else {
            BaseResponse baseResponse = new BaseResponse(0, true, output.get("RESPMSG").toString(), output);

            return echoRespose(baseResponse, HttpStatus.OK);
        }
    }


    @PostMapping("cashfree_wallet_callback")
    public ResponseEntity<Object> cashfreeWalletCallback(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            BaseResponse baseResponse = new BaseResponse(0, true, "no params found", parameterMap);
            return echoRespose(baseResponse, HttpStatus.OK);
        }
        JSONObject receivedMap = new JSONObject();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            receivedMap.put(entry.getKey(), entry.getValue()[0]);
        }
        String jsonFromCashfree = receivedMap.toString();


        return handleCashFreeCallback(jsonFromCashfree, "CASHFREE_CALLBACK");
    }

    @PostMapping("cashfree_wallet_notify")
    public ResponseEntity<Object> cashfreeWalletNotify(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            BaseResponse baseResponse = new BaseResponse(0, true, "no params found", parameterMap);
            return echoRespose(baseResponse, HttpStatus.OK);
        }
        JSONObject receivedMap = new JSONObject();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            receivedMap.put(entry.getKey(), entry.getValue()[0]);
        }
        String jsonFromCashfree = receivedMap.toString();

        return handleCashFreeCallback(jsonFromCashfree, "CASHFREE_WEBHOOK");
    }

    private ResponseEntity<Object> handleCashFreeCallback(String jsonFromCashfree, String logFrom) {
        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("STATUS", "");
        output.put("RESPMSG", "Something went wrong.");
        try {
            if (jsonFromCashfree != null && !jsonFromCashfree.isEmpty()) {
                JSONObject jsonObject = new JSONObject(jsonFromCashfree);

                if(!jsonObject.has("orderId")){
                    Util.printLog2(logFrom,jsonFromCashfree);
                    output.put("RESPMSG", "orderid not found");
                    output.put("responseData",jsonFromCashfree);
                    BaseResponse baseResponse = new BaseResponse(0, true, output.get("RESPMSG").toString(), output);
                    return echoRespose(baseResponse, HttpStatus.OK);
                }

                String order_idd = jsonObject.getString("orderId");
                if (!Util.isEmpty(order_idd)) {
                    String[] s = order_idd.split("_");

                    String customerId = s[0];
                    String promocode = s.length >= 5 ? s[4] : "";
                    jsonObject.put("promocode", promocode);

                    LinkedHashMap<String, Object> cashFreePaymentStatus = CASHFREE.checkPaymentStatus(order_idd);
                    if (cashFreePaymentStatus != null) {

                        String amount = cashFreePaymentStatus.get("orderAmount").toString();
                        String paymentMode = cashFreePaymentStatus.get("paymentMode").toString();

                        String status = (String) cashFreePaymentStatus.get("txStatus");
                        output.put("STATUS", status);
                        output.put("RESPMSG", cashFreePaymentStatus.get("txMsg"));

                        String ORDER_ID = order_idd;
                        String logRefrenceId = (String) cashFreePaymentStatus.get("referenceId");
                        String logStatus = logFrom;
                        String logPaymentGateway = "CASHFREE";
                        String logRequestJson = new Gson().toJson(new HashMap<String, String>());
                        String logResponseJson = jsonFromCashfree;

                        int customerIdInt = Integer.parseInt(customerId);
                        float amountFloat = Float.parseFloat(amount);

                        customerService.getCustomerRepository().insertPaymentLog(customerIdInt, ORDER_ID, logRefrenceId, logStatus, amountFloat, logPaymentGateway, logRequestJson, logResponseJson);

                        if (status.equals("SUCCESS")) {
                            output.put("STATUS", "TXN_SUCCESS");
                            output.put("TXNAMOUNT", amountFloat);

                            int checkTransactionIdAlreadyExist = customerService.getCustomerRepository()
                                    .checkTransactionIdAlreadyExist(customerIdInt, logRefrenceId);

                            if (checkTransactionIdAlreadyExist == 0) {

                                customerService.updateCustomerWalletRecharge(customerIdInt, amountFloat, logRefrenceId,
                                        logResponseJson, "CASHFREE", paymentMode, promocode);

                            }

                            LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerService
                                    .getCustomerRepository().getUpdatedWalletData(customerIdInt);

                            output.put("wallet", updatedWalletData.get("wallet"));
                        }

                    } else {
                        output.put("RESPMSG", "invalid order detail");
                    }

                } else {
                    output.put("RESPMSG", "invalid orderId");
                }
            } else {
                output.put("RESPMSG", "Something went wrong.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.printLog2(logFrom,jsonFromCashfree);
            output.put("RESPMSG", e.getMessage());
        }

        if (output.get("STATUS").equals("TXN_SUCCESS")) {
            BaseResponse baseResponse = new BaseResponse(0, false,
                    "Wallet reharge successfully with amount ₹ " + output.get("TXNAMOUNT"), output);

            return echoRespose(baseResponse, HttpStatus.OK);
        } else {
            BaseResponse baseResponse = new BaseResponse(0, true, output.get("RESPMSG").toString(), output);
            return echoRespose(baseResponse, HttpStatus.OK);
        }
    }
}
