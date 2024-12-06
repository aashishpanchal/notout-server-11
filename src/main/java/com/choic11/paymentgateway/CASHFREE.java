package com.choic11.paymentgateway;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.GlobalConstant.PaymentGatewayConstant;
import com.choic11.GlobalConstant.PayoutGatewayConstant;
import com.choic11.Util;
import com.choic11.curl.Curl;
import com.choic11.repository.admin.AdminRepository;
import com.choic11.service.CustomerService;
import com.google.gson.Gson;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.util.*;

public class CASHFREE {


    public static LinkedHashMap<String, Object> walletRecharge(CustomerService customerService, Integer UserId,
                                                               float amount, String referrer, String promocode, String callBackBaseurl) {

        Long time = Util.getCurrentTime();
        HashMap<String, Object> updatedProfileData = customerService.getCustomerRepository()
                .getCustomerProfileData(UserId);
        HashMap<String, String> paramList = new HashMap<String, String>();
        String ORDER_ID = UserId + "_c_w_" + time + "_" + promocode;

        HashMap<String, String> cashfreePaymentDetail = PaymentGatewayConstant.getCashfreePaymentDetail();

        String secretKey = cashfreePaymentDetail.get("CASHFREE_SECRET_KEY");

        paramList.put("appId", cashfreePaymentDetail.get("CASHFREE_APP_ID"));
        paramList.put("orderId", ORDER_ID);
        paramList.put("orderAmount", amount + "");
        paramList.put("orderCurrency", "INR");
        paramList.put("orderNote", "Wallet Recharge");
        paramList.put("customerName", URLEncoder.encode((String) updatedProfileData.get("firstname")));
        paramList.put("customerPhone", (String) updatedProfileData.get("country_mobile_code") + (String) updatedProfileData.get("phone"));
        paramList.put("customerEmail", (String) updatedProfileData.get("email"));
        paramList.put("returnUrl", callBackBaseurl + PaymentGatewayConstant.CASHFREE_PAYMENT_GETWAY_RETURN_URL);
        paramList.put("notifyUrl", callBackBaseurl + PaymentGatewayConstant.CASHFREE_PAYMENT_GETWAY_NOTIFY_URL);


        String signatureData = "";
        SortedSet<String> keys = new TreeSet<String>(paramList.keySet());
        for (String key : keys) {
            signatureData = signatureData + key + paramList.get(key);
        }

        String signature = Util.getHmac256(secretKey, signatureData);
        if (Util.isEmpty(signature)) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("ORDER_ID", "");
            data.put("logRequestJson", "");
            data.put("HTML", "getHmac256 error.");
            return data;
        }
        paramList.put("signature", signature);

        String html = "<html><head><title>" + GlobalConstant.APP_NAME
                + "</title></head><body><center style='height:100%;'>" +
                "<img src='" + GlobalConstant.PAYMENT_PAGE_LOADER + "' style='margin-top:65%;'></center>";
        html += "<form method='post' action='" + cashfreePaymentDetail.get("CASHFREE_TXN_URL")
                + "' name='f1'> <table border='1'><tbody>";

        for (Map.Entry<String, String> me : paramList.entrySet()) {
            html += "<input type='hidden' name='" + me.getKey() + "' value='" + me.getValue() + "'";
            if (me.getKey().equals("returnUrl")) {
                html += " id='return_url'>";
            } else {
                html += " >";
            }
        }

        html += "</tbody></table><script type='text/javascript'>setTimeout(function() { document.f1.submit();}, 1000); </script></form></body></html>";

        String logRequestJson = new Gson().toJson(paramList);

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("ORDER_ID", ORDER_ID);
        data.put("logRequestJson", logRequestJson);
        data.put("HTML", html);

        String logRefrenceId = "";
        String logStatus = "PAYMENT_START";
        String logPaymentGateway = "CASHFREE";
        String logResponseJson = new Gson().toJson(new HashMap<String, String>());

        customerService.getCustomerRepository().insertPaymentLog(UserId, ORDER_ID, logRefrenceId, logStatus, amount,
                logPaymentGateway, logRequestJson, logResponseJson);

        return data;

    }
    public static LinkedHashMap<String, Object> walletRechargeSdk(CustomerService customerService, Integer UserId,
                                                               float amount, String referrer, String promocode, String callBackBaseurl) {


        Long time = Util.getCurrentTime();
        HashMap<String, Object> updatedProfileData = customerService.getCustomerRepository()
                .getCustomerProfileData(UserId);

        if (Util.isEmpty((String) updatedProfileData.get("phone"))) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("ORDER_ID", "");
            data.put("logRequestJson", "");
            data.put("message", "Please update your mobile number.");
            data.put("error", true);
            return data;
        }

        HashMap<String, String> paramList = new HashMap<String, String>();
        String ORDER_ID = UserId + "_c_ws_" + time + "_" + promocode;

        HashMap<String, String> cashfreePaymentDetail = PaymentGatewayConstant.getCashfreePaymentDetail();

        String secretKey = cashfreePaymentDetail.get("CASHFREE_SECRET_KEY");
        String paymentStage = PaymentGatewayConstant.getPaymentMode();

        paramList.put("appId", cashfreePaymentDetail.get("CASHFREE_APP_ID"));
        paramList.put("orderId", ORDER_ID);
        paramList.put("orderAmount", amount + "");
        paramList.put("orderCurrency", "INR");
        paramList.put("orderNote", "Wallet Recharge");
        paramList.put("customerName", URLEncoder.encode((String) updatedProfileData.get("firstname")));
        paramList.put("customerPhone", (String) updatedProfileData.get("country_mobile_code") + (String) updatedProfileData.get("phone"));
        paramList.put("customerEmail", (String) updatedProfileData.get("email"));
        paramList.put("returnUrl", callBackBaseurl + PaymentGatewayConstant.CASHFREE_PAYMENT_GETWAY_RETURN_URL);
        paramList.put("notifyUrl", callBackBaseurl + PaymentGatewayConstant.CASHFREE_PAYMENT_GETWAY_NOTIFY_URL);


        String signatureData = "";
        SortedSet<String> keys = new TreeSet<String>(paramList.keySet());
        for (String key : keys) {
            signatureData = signatureData + key + paramList.get(key);
        }

        String signature = Util.getHmac256(secretKey, signatureData);
        if (Util.isEmpty(signature)) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("ORDER_ID", "");
            data.put("logRequestJson", "");
            data.put("message", "getHmac256 error.");
            data.put("error", true);
            return data;
        }
        paramList.put("signature", signature);
        paramList.put("paymentStage", paymentStage);

        String cftToken = cashFreePaymentCftToken(paramList);
        if (Util.isEmpty(cftToken)){
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("ORDER_ID", "");
            data.put("logRequestJson", "");
            data.put("message", "Something went wrong.");
            data.put("error", true);
            return data;
        }
        paramList.put("cftToken", cftToken);

        String logRequestJson = new Gson().toJson(paramList);

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("ORDER_ID", ORDER_ID);
        data.put("logRequestJson", logRequestJson);
        data.put("data", paramList);
        data.put("error", false);

        String logRefrenceId = "";
        String logStatus = "PAYMENT_START_SDK";
        String logPaymentGateway = "CASHFREE";
        String logResponseJson = new Gson().toJson(new HashMap<String, String>());

        customerService.getCustomerRepository().insertPaymentLog(UserId, ORDER_ID, logRefrenceId, logStatus, amount,
                logPaymentGateway, logRequestJson, logResponseJson);

        return data;

    }

    public static LinkedHashMap<String, Object> checkPaymentStatus(String orderId) {
        HashMap<String, String> cashfreePaymentDetail = PaymentGatewayConstant.getCashfreePaymentDetail();
        String curlUrl = cashfreePaymentDetail.get("CASHFREE_PAYMENT_STATUS");
        String appId = cashfreePaymentDetail.get("CASHFREE_APP_ID");
        String secretKey = cashfreePaymentDetail.get("CASHFREE_SECRET_KEY");

        JSONObject postField = new JSONObject();
        postField.put("appId", appId);
        postField.put("secretKey", secretKey);
        postField.put("orderId", orderId);

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("cache-control", "no-cache");

        Response excuteCurlRequest = Curl.excuteCurlRequestUrlEncode(curlUrl, "POST", postField, headers);
        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();
                JSONObject jsonObject = new JSONObject(responseData);

                if (jsonObject.getString("status").equalsIgnoreCase("ok")) {
                    LinkedHashMap<String, Object> output = new LinkedHashMap<>();
                    output.put("txStatus", jsonObject.get("txStatus"));
                    output.put("referenceId", jsonObject.get("referenceId"));
                    output.put("txMsg", jsonObject.get("txMsg"));
                    output.put("paymentMode", jsonObject.get("paymentMode"));
                    output.put("orderAmount", jsonObject.get("orderAmount"));
                    return output;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String cashFreePaymentCftToken(HashMap<String, String> paramList){
        HashMap<String, String> cashfreePaymentDetail = PaymentGatewayConstant.getCashfreePaymentDetail();

        String secretKey = cashfreePaymentDetail.get("CASHFREE_SECRET_KEY");
        String appId = cashfreePaymentDetail.get("CASHFREE_APP_ID");
        String url = cashfreePaymentDetail.get("CASHFREE_PAYMENT_CFTTOKEN");

        JSONObject fields = new JSONObject();
        fields.put("orderId", paramList.get("orderId"));
        fields.put("orderAmount", paramList.get("orderAmount"));
        fields.put("orderCurrency", paramList.get("orderCurrency"));

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

        headers.put("Content-Type", "application/json");
        headers.put("x-client-id", appId);
        headers.put("x-client-secret", secretKey);

        Response excuteCurlRequestPayment = Curl.excuteCurlRequest(url, "POST", fields, headers);
        JSONObject paymentResponse = null;
        if (excuteCurlRequestPayment != null) {
            try {
                String responseData = excuteCurlRequestPayment.body().string();
                if (!Util.isEmpty(responseData)) {
                    paymentResponse = new JSONObject(responseData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (paymentResponse == null || !paymentResponse.has("status") || !paymentResponse.has("cftoken")) {
            return "";
        }

        if (paymentResponse.get("status").toString().equalsIgnoreCase("ok")) {
            return (String) paymentResponse.get("cftoken");
        }
        return "";
    }

    @Autowired
    public static AdminRepository adminRepository;

    public static LinkedHashMap<String, Object> customerWithdrawAmountFromBank(HashMap<String, Object> withdrawEntryId,
                                                                               String otp, boolean isAffiliate) {

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("errorCode", 0);
        output.put("message", "");

        HashMap<String, String> payuPayoutDetail = PayoutGatewayConstant.getCashfreePayoutDetail();

        JSONObject authParam = new JSONObject();
        authParam.put("clientId", payuPayoutDetail.get("CASHFREE_PAYOUT_CLIENT_ID"));
        authParam.put("clientSecret", otp);
        authParam.put("stage", payuPayoutDetail.get("CASHFREE_PAYOUT_STAGE"));

        CfPayout cfPayout;
        try {
            cfPayout = new CfPayout(authParam);
        } catch (Exception e) {
            output.put("errorCode", 1);
            output.put("message", e.getMessage());
            return output;
        }

        if(cfPayout.token_error!=null){
            output.put("errorCode", 1);
            output.put("message", cfPayout.token_error);
            return output;
        }

        Long currentTime = Util.getCurrentTime();

        int customerId = (int) withdrawEntryId.get("customerId");
        int entryId = (int) withdrawEntryId.get("id");
        float amount = (float) withdrawEntryId.get("amount");
        float instantFee = (float) withdrawEntryId.get("instantFee");
        float needTransferAmount = amount - instantFee;
        String bankAccountNumber = (String) withdrawEntryId.get("bankAccountNumber");
        String bankIfsc = (String) withdrawEntryId.get("bankIfsc");
        String bankHolderName = (String) withdrawEntryId.get("bankHolderName");

        String customerEmail = "";
        String customerPhone = "";

        if (withdrawEntryId.get("email") != null) {
            customerEmail = (String) withdrawEntryId.get("email");
        }
        if (withdrawEntryId.get("phone") != null) {
            customerPhone = (String) withdrawEntryId.get("phone");
        }

        String purpose = GlobalConstant.APP_NAME + " Payout";
        String merchantRefId = entryId + "_" + bankAccountNumber + "_123";
        String batchId = String.valueOf(entryId);
        if (isAffiliate) {
            purpose = GlobalConstant.APP_NAME + " AFFILIATE Payout";
            merchantRefId = entryId + "_" + bankAccountNumber + "_AF_123";
            batchId = String.valueOf(entryId) + "_AF";
        }

        JSONObject beneficiary = new JSONObject();
        beneficiary.put("beneId", customerId + "_" + bankAccountNumber);
        beneficiary.put("name", bankHolderName);
        beneficiary.put("email", customerEmail);
        beneficiary.put("phone", customerPhone);
        beneficiary.put("bankAccount", bankAccountNumber);
        beneficiary.put("ifsc", bankIfsc);
        beneficiary.put("address1", bankAccountNumber);

        JSONObject beneficiaryDetail = cfPayout.isBeneficiaryExist(beneficiary.getString("beneId"));
        if(((String)beneficiaryDetail.get("status")).equalsIgnoreCase("FAILED")){
            output.put("errorCode",2);
            output.put("message", beneficiaryDetail.get("message"));
            return output;
        }
        if(((String)beneficiaryDetail.get("status")).equalsIgnoreCase("ERROR") && (beneficiaryDetail.get("subCode")).equals("404")){
            JSONObject response = cfPayout.addBeneficiary(beneficiary);
            if (!response.get("status").equals("SUCCESS")) {
                output.put("errorCode",3);
                output.put("message", response.get("message"));
                return output;
            }
            beneficiaryDetail.put("status","SUCCESS");
        }

        if (!beneficiaryDetail.get("status").equals("SUCCESS")) {
            output.put("errorCode",4);
            output.put("message", beneficiaryDetail.get("message"));
            return output;
        }

        JSONObject transfer = new JSONObject();
        transfer.put("beneId", beneficiary.getString("beneId"));
        transfer.put("amount", needTransferAmount);
        transfer.put("transferId", merchantRefId);
        transfer.put("remarks", purpose);
        JSONObject responseTransfer = cfPayout.requestTransfer(transfer);

        if(responseTransfer.isEmpty()){
            responseTransfer= new JSONObject();
            responseTransfer.put("status","PENDING");
            responseTransfer.put("message","No response from thirdparty");
        }
        responseTransfer.put("localtransferId",transfer.get("transferId"));
        responseTransfer.put("transferRequest",transfer);

        if(!responseTransfer.get("status").equals("SUCCESS") && !responseTransfer.get("status").equals("PENDING")){
            output.put("errorCode",5);
            output.put("message", responseTransfer.get("message"));
            return output;
        }

        String return_message="Transaction success.";
        String finalStatus="C";
        if(responseTransfer.get("status").equals("PENDING")){
            return_message ="Transaction on hold please check after some time.";
            finalStatus="H";
        }


        String referenceId= responseTransfer.getString("localtransferId");
        String utr = "";
        if (responseTransfer.has("data")){
            utr = responseTransfer.getJSONObject("data").getString("utr");
        }
        JSONObject json_data = responseTransfer;

        output.put("completed", true);
        output.put("merchantRefId", referenceId);
        output.put("utr", utr);
        output.put("jsonData", json_data.toString());
        output.put("message", return_message);
        output.put("finalStatus", finalStatus);
        return output;

    }
}
