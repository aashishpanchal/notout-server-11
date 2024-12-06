package com.choic11.sms;

import com.choic11.GlobalConstant.SmsConstant;
import com.choic11.Util;
import com.choic11.curl.Curl;
import com.choic11.model.TblTemplate;
import okhttp3.Response;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;

public class SmsUtil {

    public static String sendSms(TblTemplate tblTemplate, String to, String country_code, HashMap<String, Object> data) {

        if (Util.isEmpty(SmsConstant.CURRENT_SMS_PROVIDER)) {
            return "CURRENT_SMS_PROVIDER";
        }
        String text = tblTemplate.getContent();
        String otp = "";
        if (data.containsKey("otp")) {
            otp = data.get("otp").toString();
        }

        if (SmsConstant.CURRENT_SMS_PROVIDER.equals(SmsConstant.SMS_PROVIDER_TEXT_LOCAL)) {
            return sendSmsTextLocal(text, to, country_code);
        } else if (SmsConstant.CURRENT_SMS_PROVIDER.equals(SmsConstant.SMS_PROVIDER_FAST2SMS)) {
            return sendSmsFast2Sms(tblTemplate.getMessageId(), otp, to);
        }

        return "CURRENT_SMS_PROVIDER_NOT_MATCH";

    }

    public static String sendSmsTextLocal(String text, String to, String country_code) {
        if (Util.isEmpty(SmsConstant.SMS_SENDER_NAME_TEXT_LOCAL)) {
            return "SMS_SENDER_NAME";
        }

        country_code = country_code.replace("+", "");

        to = country_code + to;

        text = text.replaceAll("\\<.*?\\>", "");

        String sender = URLEncoder.encode(SmsConstant.SMS_SENDER_NAME_TEXT_LOCAL);

        String message = URLEncoder.encode(text);

        String finalUrl = "https://api.textlocal.in/send/?apiKey=" + SmsConstant.SMS_KEY_TEXT_LOCAL + "&sender="
                + sender + "&numbers=" + to + "&message=" + message;

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(finalUrl, "GET", jsonBody, null);
        if (excuteCurlRequest != null) {
            try {
                String response = excuteCurlRequest.body().string();

                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "FAILED";

    }

    public static String sendSmsFast2Sms(String messageId, String variableValue, String to) {
        if (Util.isEmpty(SmsConstant.SMS_SENDER_NAME_FAST2SMS)) {
            return "SMS_SENDER_NAME";
        }

        String apiUrl = "https://www.fast2sms.com/dev/bulkV2?";

        String authorization = SmsConstant.SMS_KEY_FAST2SMS;

        String route = "dlt";
        String sender_id = SmsConstant.SMS_SENDER_NAME_FAST2SMS;
        String message = messageId;
        String variables_values = URLEncoder.encode(variableValue + "|");
        String numbers = to;
        String flash = "0";

        String finalUrl = apiUrl + "authorization=" + authorization + "&route=" + route + "&sender_id=" + sender_id + "&message=" + message + "&variables_values=" + variables_values + "&flash=" + flash + "&numbers=" + numbers;

        JSONObject jsonBody = null;
        Response excuteCurlRequest = Curl.excuteCurlRequest(finalUrl, "GET", jsonBody, null);
        if (excuteCurlRequest != null) {
            try {
                String response = excuteCurlRequest.body().string();

                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "FAILED";
    }

}
