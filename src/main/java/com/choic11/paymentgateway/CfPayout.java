package com.choic11.paymentgateway;

import com.choic11.Util;
import com.choic11.curl.Curl;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class CfPayout {

    protected String token;
    protected String token_error;
    protected String baseUrl;

    public CfPayout() {
    }

    public CfPayout(JSONObject authParam) {
        if (authParam == null && authParam.isEmpty()) {
            token_error = "Authorization failed. authParam not found.";
            return;
        }
        String clientId = authParam.getString("clientId");
        String clientSecret = authParam.getString("clientSecret");
        String stage = authParam.getString("stage");
        if (stage.equals("PROD")) {
            baseUrl = "https://payout-api.cashfree.com/payout/v1";
        } else {
            baseUrl = "https://payout-gamma.cashfree.com/payout/v1";
        }

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Client-Id", clientId);
        headers.put("X-Client-Secret", clientSecret);

        String endpoint = baseUrl + "/authorize";
        Response excuteCurlRequest = Curl.excuteCurlRequest(endpoint,
                "POST", new JSONObject(), headers);

        if (excuteCurlRequest != null) {
            try {
                String responseData = excuteCurlRequest.body().string();
                if (!Util.isEmpty(responseData)) {
                    JSONObject jsonObject = new JSONObject(responseData);
                    if (jsonObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                        token = (String) jsonObject.getJSONObject("data").get("token");
                    } else {
                        token_error = "Authorization failed. Reason : " + jsonObject.get("message");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(token== null && token_error==null){
            token_error = "Authorization failed. Reason : no message";
        }
    }

    public JSONObject addBeneficiary(JSONObject body) {
        JSONObject response = new JSONObject();
        response.put("status", "FAILED");
        response.put("message", "Authorization failed");
        if (!Util.isEmpty(token)) {
            String endpoint = baseUrl + "/addBeneficiary";
            String authToken = token;
            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put("Authorization", "Bearer " + authToken);
            Response excuteCurlRequest = Curl.excuteCurlRequest(endpoint,
                    "POST", body, headers);
            if (excuteCurlRequest != null) {
                try {
                    String responseData = excuteCurlRequest.body().string();
                    if (!Util.isEmpty(responseData)) {
                        JSONObject jsonObject = new JSONObject(responseData);
                        return jsonObject;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!Util.isEmpty(token_error)) {
                response.put("message", token_error);
            }
        }
        return response;
    }

    public JSONObject isBeneficiaryExist(String benId) {
        JSONObject response = new JSONObject();
        response.put("status", "FAILED");
        response.put("message", "Authorization failed");
        if (!Util.isEmpty(token)) {
            String endpoint = baseUrl + "/getBeneficiary/" + benId;
            String authToken = token;
            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put("Authorization", "Bearer " + authToken);
            JSONObject body = new JSONObject();
            Response excuteCurlRequest = Curl.excuteCurlRequest(endpoint, "GET", body, headers);
            if (excuteCurlRequest != null) {
                try {
                    String responseData = excuteCurlRequest.body().string();
                    if (!Util.isEmpty(responseData)) {
                        JSONObject jsonObject = new JSONObject(responseData);
                        return jsonObject;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!Util.isEmpty(token_error)) {
                response.put("message", token_error);
            }
        }
        return response;
    }

    public JSONObject requestTransfer(JSONObject body) {
        JSONObject response = new JSONObject();
        response.put("status", "FAILED");
        response.put("message", "Authorization failed");
        if (!Util.isEmpty(token)) {
            String endpoint = baseUrl + "/requestTransfer";
            String authToken = token;
            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put("Authorization", "Bearer " + authToken);
            Response excuteCurlRequest = Curl.excuteCurlRequest(endpoint,
                    "POST", body, headers);
            if (excuteCurlRequest != null) {
                try {
                    String responseData = excuteCurlRequest.body().string();
                    if (!Util.isEmpty(responseData)) {
                        JSONObject jsonObject = new JSONObject(responseData);
                        return jsonObject;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!Util.isEmpty(token_error)) {
                response.put("message", token_error);
            }
        }
        return response;
    }
}
