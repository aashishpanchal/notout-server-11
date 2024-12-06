package com.choic11.GlobalConstant;

import java.util.HashMap;

public class PayoutGatewayConstant {


	public static final String PAYOUT_METHOD_PAYU = "PAYU";
	public static final String PAYOUT_METHOD_PAYTM = "PAYTM";
	public static final String PAYOUT_METHOD_CASHFREE = "CASHFREE";
	public static final String PAYOUT_METHOD_DIRECT = "DIRECT";

	public static final String CURRENT_PAYOUT_METHOD = PAYOUT_METHOD_CASHFREE;



	public static final String PAYU_PAYOUT_CLIENT_ID = "";
	public static final String PAYU_PAYOUT_USERNAME = "";
	public static final String PAYU_PAYOUT_PASSWORD = "";
	public static final String PAYU_PAYOUT_MERCHANTID = "";
	public static final String PAYU_PAYOUT_AUTH_TOKEN_URL = "https://accounts.payu.in/oauth/token";
	public static final String PAYU_PAYOUT_URL = "https://www.payumoney.com/payout/";

	public static final String PAYU_PAYOUT_CLIENT_ID_TEST = "";
	public static final String PAYU_PAYOUT_USERNAME_TEST = "";
	public static final String PAYU_PAYOUT_PASSWORD_TEST = "";
	public static final String PAYU_PAYOUT_MERCHANTID_TEST = "";
	public static final String PAYU_PAYOUT_AUTH_TOKEN_URL_TEST = "https://uat-accounts.payu.in/oauth/token";
	public static final String PAYU_PAYOUT_URL_TEST = "https://test.payumoney.com/payout/";

	public static final HashMap<String, String> getPayuPayoutDetail() {

		HashMap<String, String> output = new HashMap<String, String>();
		if (!GlobalConstant.isProjectTypeProd()) {
			output.put("PAYU_PAYOUT_CLIENT_ID", PAYU_PAYOUT_CLIENT_ID_TEST);
			output.put("PAYU_PAYOUT_USERNAME", PAYU_PAYOUT_USERNAME_TEST);
			output.put("PAYU_PAYOUT_PASSWORD", PAYU_PAYOUT_PASSWORD_TEST);
			output.put("PAYU_PAYOUT_MERCHANTID", PAYU_PAYOUT_MERCHANTID_TEST);
			output.put("PAYU_PAYOUT_AUTH_TOKEN_URL", PAYU_PAYOUT_AUTH_TOKEN_URL_TEST);
			output.put("PAYU_PAYOUT_URL", PAYU_PAYOUT_URL_TEST);
		} else {
			output.put("PAYU_PAYOUT_CLIENT_ID", PAYU_PAYOUT_CLIENT_ID);
			output.put("PAYU_PAYOUT_USERNAME", PAYU_PAYOUT_USERNAME);
			output.put("PAYU_PAYOUT_PASSWORD", PAYU_PAYOUT_PASSWORD);
			output.put("PAYU_PAYOUT_MERCHANTID", PAYU_PAYOUT_MERCHANTID);
			output.put("PAYU_PAYOUT_AUTH_TOKEN_URL", PAYU_PAYOUT_AUTH_TOKEN_URL);
			output.put("PAYU_PAYOUT_URL", PAYU_PAYOUT_URL);
		}
		return output;
	}
	
	
	public static final String PAYTM_PAYOUT_GUID="";
	public static final String PAYTM_PAYOUT_MID="CHOIC135949060715715";
	public static final String PAYTM_PAYOUT_MKEY="En45JCtJPAWxuz8U";
	public static final String PAYTM_PAYOUT_URL="https://dashboard.paytm.com/bpay/api/v1/disburse/order/wallet/gratification";
	public static final String PAYTM_PAYOUT_STATUS_URL="https://dashboard.paytm.com/bpay/api/v1/disburse/order/query";
	
	public static final String PAYTM_PAYOUT_GUID_TEST="e8231943-cdd7-11eb-9947-fa163e429e83";
	public static final String PAYTM_PAYOUT_MID_TEST="CHOIC169838102656792";
	public static final String PAYTM_PAYOUT_MKEY_TEST="pr@jMfYPH4LaV31y";
	public static final String PAYTM_PAYOUT_URL_TEST="https://staging-dashboard.paytm.com/bpay/api/v1/disburse/order/wallet/gratification";
	public static final String PAYTM_PAYOUT_STATUS_URL_TEST="https://staging-dashboard.paytm.com/bpay/api/v1/disburse/order/query";
	
	public static final HashMap<String, String> getPaytmPayoutDetail() {

		HashMap<String, String> output = new HashMap<String, String>();
		if (!GlobalConstant.isProjectTypeProd()) {
			output.put("PAYTM_PAYOUT_GUID", PAYTM_PAYOUT_GUID_TEST);
			output.put("PAYTM_PAYOUT_MID", PAYTM_PAYOUT_MID_TEST);
			output.put("PAYTM_PAYOUT_MKEY", PAYTM_PAYOUT_MKEY_TEST);
			output.put("PAYTM_PAYOUT_URL", PAYTM_PAYOUT_URL_TEST);
			output.put("PAYTM_PAYOUT_STATUS_URL", PAYTM_PAYOUT_STATUS_URL_TEST);
		} else {
			output.put("PAYTM_PAYOUT_GUID", PAYTM_PAYOUT_GUID);
			output.put("PAYTM_PAYOUT_MID", PAYTM_PAYOUT_MID);
			output.put("PAYTM_PAYOUT_MKEY", PAYTM_PAYOUT_MKEY);
			output.put("PAYTM_PAYOUT_URL", PAYTM_PAYOUT_URL);
			output.put("PAYTM_PAYOUT_STATUS_URL", PAYTM_PAYOUT_STATUS_URL);
		}
		return output;
	}

	public static final String CASHFREE_PAYOUT_CLIENT_ID = "CF100261C1FH3OLQN2TFPS1H1R0G";
	public static final String CASHFREE_PAYOUT_CLIENT_SECRET = "";
	public static final String CASHFREE_PAYOUT_STAGE = "PROD";

	public static final String CASHFREE_PAYOUT_CLIENT_ID_TEST = "CF54775C1FHMOFA55OA211PLMUG";
	public static final String CASHFREE_PAYOUT_CLIENT_SECRET_TEST = "6bf341e5a55b9738ec24feba40fe61ba40649302";
	public static final String CASHFREE_PAYOUT_STAGE_TEST = "STAG";

	public static final HashMap<String, String> getCashfreePayoutDetail() {

		HashMap<String, String> output = new HashMap<String, String>();
		if (!GlobalConstant.isProjectTypeProd()) {
			output.put("CASHFREE_PAYOUT_CLIENT_ID", CASHFREE_PAYOUT_CLIENT_ID_TEST);
			output.put("CASHFREE_PAYOUT_STAGE", CASHFREE_PAYOUT_STAGE_TEST);
		} else {
			output.put("CASHFREE_PAYOUT_CLIENT_ID", CASHFREE_PAYOUT_CLIENT_ID);
			output.put("CASHFREE_PAYOUT_STAGE", CASHFREE_PAYOUT_STAGE);
		}
		return output;
	}
}
