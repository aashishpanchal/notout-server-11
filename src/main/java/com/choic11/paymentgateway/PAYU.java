package com.choic11.paymentgateway;

import com.google.gson.Gson;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.GlobalConstant.PaymentGatewayConstant;
import com.choic11.GlobalConstant.PayoutGatewayConstant;
import com.choic11.Util;
import com.choic11.curl.Curl;
import com.choic11.service.CustomerService;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PAYU {

    public static LinkedHashMap<String, Object> walletRecharge(CustomerService customerService, Integer UserId,
                                                               float amount, String referrer, String promocode, String callBackBaseurl) {

        Long time = Util.getCurrentTime();
        HashMap<String, Object> updatedProfileData = customerService.getCustomerRepository()
                .getCustomerProfileData(UserId);
        HashMap<String, String> paramList = new HashMap<String, String>();
        String ORDER_ID = UserId + "_customer_wallet_" + time;
        Float TXN_AMOUNT = amount;
        HashMap<String, String> payuPaymentDetail = PaymentGatewayConstant.getPayuPaymentDetail();

        paramList.put("key", payuPaymentDetail.get("PAYU_MERCHANT_KEY"));
        paramList.put("txnid", ORDER_ID);
        paramList.put("amount", amount + "");
        paramList.put("firstname", updatedProfileData.get("firstname") + "");
        paramList.put("email", updatedProfileData.get("email") + "");
        paramList.put("phone", updatedProfileData.get("phone") + "");
        paramList.put("productinfo", "Recharge Wallet.");
        paramList.put("service_provider", "payu_paisa");
        paramList.put("surl", callBackBaseurl + PaymentGatewayConstant.PAYU_PAYMENT_GETWAY_RETURN_URL);
        paramList.put("furl", callBackBaseurl + PaymentGatewayConstant.PAYU_PAYMENT_GETWAY_RETURN_URL);
        paramList.put("udf1", referrer);
        paramList.put("udf2", promocode);
        paramList.put("udf3", UserId + "");

        String hashSequence = "key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|udf6|udf7|udf8|udf9|udf10";
        String[] hashSequence1 = hashSequence.split("\\|");
        String hashString = "";
        for (String string : hashSequence1) {
            hashString += paramList.containsKey(string) ? paramList.get(string) : "";
            hashString += "|";
        }
        hashString += payuPaymentDetail.get("PAYU_SALT");
        String hash = Util.getSHA512(hashString);
        hash = hash.toLowerCase();
        paramList.put("hash", hash);

        String html = "<html><head><title>" + GlobalConstant.APP_NAME
                + "</title></head><body><center><h1>Please do not refresh this page...</h1></center>";
        html += "<form method='post' action='" + payuPaymentDetail.get("PAYU_TXN_URL")
                + "' name='f1'> <table border='1'><tbody>";

        for (Map.Entry<String, String> me : paramList.entrySet()) {
            html += "<input type='hidden' name='" + me.getKey() + "' value='" + me.getValue() + "'";
            if (me.getKey().equals("surl")) {
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
        String logPaymentGateway = "PAYU";
        String logResponseJson = new Gson().toJson(new HashMap<String, String>());

        customerService.getCustomerRepository().insertPaymentLog(UserId, ORDER_ID, logRefrenceId, logStatus, amount,
                logPaymentGateway, logRequestJson, logResponseJson);

        return data;

    }

    public static LinkedHashMap<String, Object> customerWithdrawAmountFromBank(HashMap<String, Object> withdrawEntryId,
                                                                               String otp, boolean isAffiliate) {

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("errorCode", 0);
        output.put("message", "");

        HashMap<String, String> payuPayoutDetail = PayoutGatewayConstant.getPayuPayoutDetail();

        JSONObject authParam = new JSONObject();
        authParam.put("grant_type", "password");
        authParam.put("scope", "create_payout_transactions");
        authParam.put("client_id", payuPayoutDetail.get("PAYU_PAYOUT_CLIENT_ID"));
        authParam.put("username", payuPayoutDetail.get("PAYU_PAYOUT_USERNAME"));
        authParam.put("password", otp);

        Response excuteCurlRequest = Curl.excuteCurlRequestUrlEncode(payuPayoutDetail.get("PAYU_PAYOUT_AUTH_TOKEN_URL"),
                "POST", authParam, null);
        String authToken = "";
        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();

                if (!Util.isEmpty(responseData)) {
                    JSONObject jsonObject = new JSONObject(responseData);
                    authToken = jsonObject.optString("access_token", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Util.isEmpty(authToken)) {
            output.put("errorCode", 0);
            output.put("message", "Authorization failed");
            return output;
        }

        Long currentTime = Util.getCurrentTime();

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
        String merchantRefId = entryId + "_" + bankAccountNumber + "_" + currentTime;
        String batchId = String.valueOf(entryId);
        if (isAffiliate) {
            purpose = GlobalConstant.APP_NAME + " AFFILIATE Payout";
            merchantRefId = entryId + "_" + bankAccountNumber + "_AF_" + currentTime;
            batchId = String.valueOf(entryId) + "_AF";
        }

        JSONObject transferRequest = new JSONObject();
        transferRequest.put("beneficiaryAccountNumber", bankAccountNumber);
        transferRequest.put("beneficiaryIfscCode", bankIfsc);
        transferRequest.put("beneficiaryName", bankHolderName);
        transferRequest.put("beneficiaryEmail", customerEmail);
        transferRequest.put("beneficiaryMobile", customerPhone);
        transferRequest.put("purpose", purpose);
        transferRequest.put("amount", needTransferAmount);
        transferRequest.put("batchId", batchId);
        transferRequest.put("merchantRefId", merchantRefId);
        transferRequest.put("paymentType", "IMPS");

        JSONArray transfer = new JSONArray();
        transfer.put(transferRequest);
        String paymentUrl = payuPayoutDetail.get("PAYU_PAYOUT_URL") + "payment";

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

        headers.put("authorization", "Bearer " + authToken);
        headers.put("payoutMerchantId", payuPayoutDetail.get("PAYU_PAYOUT_MERCHANTID"));

        Response excuteCurlRequestPayment = Curl.excuteCurlRequest(paymentUrl, "POST", transfer, headers);

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

        if (paymentResponse == null || !paymentResponse.has("status")) {
            output.put("errorCode", 0);
            output.put("message", "Payment response not found.");
            return output;
        }

        if (paymentResponse.getInt("status") != 0) {
            output.put("errorCode", paymentResponse.get("status"));
            String msg = paymentResponse.optString("msg");
            if (Util.isEmpty(msg)) {
                msg = paymentResponse.toString();
            }
            output.put("message", msg);
            return output;
        }

        output.put("completed", true);
        output.put("merchantRefId", merchantRefId);
        output.put("utr", merchantRefId);
        output.put("jsonData", paymentResponse.toString());

        String msg = paymentResponse.optString("msg");
        if (Util.isEmpty(msg)) {
            msg = paymentResponse.optString("message");
        }
        output.put("message", msg);
        return output;
    }

}
