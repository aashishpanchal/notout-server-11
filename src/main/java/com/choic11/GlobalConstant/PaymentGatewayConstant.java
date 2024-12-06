package com.choic11.GlobalConstant;

import java.util.HashMap;

public class PaymentGatewayConstant {

    public static final String PAYMENT_METHOD_PAYU = "PAYU";
    public static final String PAYMENT_METHOD_CASHFREE = "CASHFREE";

    public static final String CURRENT_PAYMENT_METHOD = PAYMENT_METHOD_CASHFREE;

    public static final String PAYU_TXN_URL = "https://test.payu.in/_payment";
    public static final String PAYU_MERCHANT_KEY = "";
    public static final String PAYU_SALT = "";
    public static final String PAYU_TXN_URL_LIVE = "https://secure.payu.in/_payment";
    public static final String PAYU_MERCHANT_KEY_LIVE = "";
    public static final String PAYU_SALT_LIVE = "";

    public static final String PAYU_PAYMENT_GETWAY_RETURN_URL = "payu_wallet_callback";

    public static final HashMap<String, String> getPayuPaymentDetail() {

        HashMap<String, String> output = new HashMap<String, String>();
        if (!GlobalConstant.isProjectTypeProd()) {
            output.put("PAYU_TXN_URL", PAYU_TXN_URL);
            output.put("PAYU_MERCHANT_KEY", PAYU_MERCHANT_KEY);
            output.put("PAYU_SALT", PAYU_SALT);
        } else {
            output.put("PAYU_TXN_URL", PAYU_TXN_URL_LIVE);
            output.put("PAYU_MERCHANT_KEY", PAYU_MERCHANT_KEY_LIVE);
            output.put("PAYU_SALT", PAYU_SALT_LIVE);
        }

        return output;

    }

    public static final String CASHFREE_TXN_URL = "https://test.cashfree.com/billpay/checkout/post/submit";
    public static final String CASHFREE_SECRET_KEY = "135d1e8ade811fd3715d0c5236990a3a191fe4c0";
    public static final String CASHFREE_APP_ID = "54775c059e374993a9ef88eba57745";
    public static final String CASHFREE_PAYMENT_STATUS = "https://test.cashfree.com/api/v1/order/info/status";
    public static final String CASHFREE_PAYMENT_CFTTOKEN = "https://test.cashfree.com/api/v2/cftoken/order";

    public static final String CASHFREE_TXN_URL_LIVE = "https://www.cashfree.com/checkout/post/submit";
    public static final String CASHFREE_SECRET_KEY_LIVE = "3e2000f499b0b49fb4b5b89d18442d72e2f58ce8";
    public static final String CASHFREE_APP_ID_LIVE = "100261126641126793e52ab545162001";
    public static final String CASHFREE_PAYMENT_STATUS_LIVE = "https://api.cashfree.com/api/v1/order/info/status";
    public static final String CASHFREE_PAYMENT_CFTTOKEN_LIVE = "https://api.cashfree.com/api/v2/cftoken/order";

    public static final String CASHFREE_PAYMENT_GETWAY_RETURN_URL = "cashfree_wallet_callback";
    public static final String CASHFREE_PAYMENT_GETWAY_NOTIFY_URL = "cashfree_wallet_notify";

    public static final HashMap<String, String> getCashfreePaymentDetail() {

        HashMap<String, String> output = new HashMap<String, String>();
        if (!GlobalConstant.isProjectTypeProd()) {
            output.put("CASHFREE_TXN_URL", CASHFREE_TXN_URL);
            output.put("CASHFREE_SECRET_KEY", CASHFREE_SECRET_KEY);
            output.put("CASHFREE_APP_ID", CASHFREE_APP_ID);
            output.put("CASHFREE_PAYMENT_STATUS", CASHFREE_PAYMENT_STATUS);
            output.put("CASHFREE_PAYMENT_CFTTOKEN", CASHFREE_PAYMENT_CFTTOKEN);
        } else {
            output.put("CASHFREE_TXN_URL", CASHFREE_TXN_URL_LIVE);
            output.put("CASHFREE_SECRET_KEY", CASHFREE_SECRET_KEY_LIVE);
            output.put("CASHFREE_APP_ID", CASHFREE_APP_ID_LIVE);
            output.put("CASHFREE_PAYMENT_STATUS", CASHFREE_PAYMENT_STATUS_LIVE);
            output.put("CASHFREE_PAYMENT_CFTTOKEN", CASHFREE_PAYMENT_CFTTOKEN_LIVE);
        }

        return output;

    }

    public static final String getPaymentMode(){
        if (!GlobalConstant.isProjectTypeProd()) {
                return "TEST";
        } else {
            return "PROD";
        }
    }


}
