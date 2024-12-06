package com.choic11.paymentgateway;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.GlobalConstant.PayoutGatewayConstant;
import com.choic11.Util;
import com.choic11.curl.Curl;
import com.paytm.pg.merchant.PaytmChecksum;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Paytm {

	public static LinkedHashMap<String, Object> customerWithdrawAmountFromBank(HashMap<String, Object> withdrawEntryId,
			String otp, boolean isAffiliate) {

		LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("errorCode", 0);
		output.put("message", "");

		int entryId = (int) withdrawEntryId.get("id");
		float amount = (float) withdrawEntryId.get("amount");
		float instantFee = (float) withdrawEntryId.get("instantFee");
		float needTransferAmount = amount - instantFee;
		String paytmphone = (String) withdrawEntryId.get("paytmphone");
		String paytmPayoutCallBackUrl = (String) withdrawEntryId.get("paytmPayoutCallBackUrl")+"paytm_payout_webhook";

		String comments = GlobalConstant.APP_NAME + " Payout";
		String orderId = entryId + "_ORDER";
		if (isAffiliate) {
			comments = GlobalConstant.APP_NAME + " AFFILIATE Payout";
			orderId = entryId + "_ORDER_AF";
		}
		
		HashMap<String, String> paytmPayoutDetail = PayoutGatewayConstant.getPaytmPayoutDetail();

		JSONObject paytmParams = new JSONObject();
		paytmParams.put("subwalletGuid", otp);
		paytmParams.put("orderId", orderId);
		paytmParams.put("beneficiaryPhoneNo", paytmphone);
		paytmParams.put("amount", String.valueOf(needTransferAmount));
		paytmParams.put("callbackUrl", paytmPayoutCallBackUrl);
		paytmParams.put("comments", comments);
		paytmParams.put("maxQueueDays", 0);

		String post_data = paytmParams.toString();
		String checksum = "";
		try {
			checksum = PaytmChecksum.generateSignature(post_data, paytmPayoutDetail.get("PAYTM_PAYOUT_MKEY"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Util.isEmpty(checksum)) {
			output.put("errorCode", 1);
			output.put("message", "Checksum failed");
			return output;
		}

		String x_mid = paytmPayoutDetail.get("PAYTM_PAYOUT_MID");
		String x_checksum = checksum;

		String paymentUrl = paytmPayoutDetail.get("PAYTM_PAYOUT_URL");

		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

		headers.put("x-mid", x_mid);
		headers.put("x-checksum", x_checksum);

		Response excuteCurlRequestPayment = Curl.excuteCurlRequest(paymentUrl, "POST", paytmParams, headers);

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
			output.put("errorCode", 2);
			output.put("message", "Payment response not found.");
			return output;
		}
		String paytmStatus=paymentResponse.getString("status");
		String paytmstatusCode=paymentResponse.getString("statusCode");
		String paytmstatusMessage=paymentResponse.getString("statusMessage");

		if (paytmStatus.equals("FAILURE") || paytmStatus.equals("CANCELLED") || paytmStatus.equals("UNAUTHORIZED")) {
			output.put("errorCode", 3);
			String msg = paytmstatusCode+" "+paytmstatusMessage;
			output.put("message", msg);
			return output;
		}
		
		output.put("completed", true);
		output.put("merchantRefId", orderId);
		output.put("utr", orderId);
		output.put("jsonData", paymentResponse.toString());
		output.put("message", paytmstatusMessage);
		return output;
	}
	
	public static LinkedHashMap<String, Object> getPayoutStatusByOrderId(String orderId) {
		
		LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("errorCode", 0);
		output.put("message", "");
		
		HashMap<String, String> paytmPayoutDetail = PayoutGatewayConstant.getPaytmPayoutDetail();
		
		JSONObject paytmParams = new JSONObject();
		paytmParams.put("orderId", orderId);

		String post_data = paytmParams.toString();
		String checksum = "";
		try {
			checksum = PaytmChecksum.generateSignature(post_data, paytmPayoutDetail.get("PAYTM_PAYOUT_MKEY"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Util.isEmpty(checksum)) {
			output.put("errorCode", 4);
			output.put("message", "Status Checksum failed");
			return output;
		}

		String x_mid = paytmPayoutDetail.get("PAYTM_PAYOUT_MID");
		String x_checksum = checksum;

		String paymentUrl = paytmPayoutDetail.get("PAYTM_PAYOUT_STATUS_URL");

		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

		headers.put("x-mid", x_mid);
		headers.put("x-checksum", x_checksum);

		Response excuteCurlRequestPayment = Curl.excuteCurlRequest(paymentUrl, "POST", paytmParams, headers);

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
			output.put("errorCode", 5);
			output.put("message", "Payment status response not found.");
			return output;
		}
		
		String paytmStatus=paymentResponse.getString("status");
		String paytmstatusCode=paymentResponse.getString("statusCode");
		String paytmstatusMessage=paymentResponse.getString("statusMessage");

		String referenceId=orderId;
        String utr=orderId;
		if(paymentResponse.has("result")) {
			JSONObject result=paymentResponse.getJSONObject("result");
			referenceId=result.optString("orderId",orderId);
			utr=result.optString("paytmOrderId",orderId);
		}

		if (paytmStatus.equals("FAILURE") || paytmStatus.equals("CANCELLED")) {
			output.put("errorCode", 6);
			output.put("merchantRefId", referenceId);
			output.put("utr", utr);
			output.put("jsonData", paymentResponse.toString());
			String msg = paytmstatusCode+" "+paytmstatusMessage;
			output.put("message", msg);
			return output;
		}else if (paytmStatus.equals("SUCCESS")) {
			output.put("completed", true);
			output.put("errorCode", 0);
			output.put("merchantRefId", referenceId);
			output.put("utr", utr);
			output.put("jsonData", paymentResponse.toString());
			output.put("message", paytmstatusMessage);
			return output;
		}else {
			output.put("errorCode", 7);
			output.put("merchantRefId", referenceId);
			output.put("utr", utr);
			output.put("jsonData", paymentResponse.toString());
			String msg = paytmstatusCode+" "+paytmstatusMessage;
			output.put("message", msg);
			return output;
		}
	}

}
