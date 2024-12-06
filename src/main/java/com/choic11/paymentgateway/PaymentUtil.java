package com.choic11.paymentgateway;

import com.choic11.GlobalConstant.PaymentGatewayConstant;
import com.choic11.service.CustomerService;

import java.util.LinkedHashMap;

public class PaymentUtil {

    public static LinkedHashMap<String, Object> walletRecharge(CustomerService customerService, Integer UserId,
                                                               float amount, String referrer, String promocode, String callBackBaseurl, String paymentGateway) {

        if (paymentGateway.equals(PaymentGatewayConstant.PAYMENT_METHOD_PAYU)) {
            return PAYU.walletRecharge(customerService, UserId, amount, referrer, promocode, callBackBaseurl);
        } else if (paymentGateway.equals(PaymentGatewayConstant.PAYMENT_METHOD_CASHFREE)) {
            return CASHFREE.walletRecharge(customerService, UserId, amount, referrer, promocode, callBackBaseurl);
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("ORDER_ID", "");
        data.put("logRequestJson", "");
        data.put("HTML", "!!!coming soon!!!");

        return data;
    }
    public static LinkedHashMap<String, Object> walletRechargeSdk(CustomerService customerService, Integer UserId,
                                                               float amount, String referrer, String promocode, String callBackBaseurl, String paymentGateway) {

        if (paymentGateway.equals(PaymentGatewayConstant.PAYMENT_METHOD_CASHFREE)) {
            return CASHFREE.walletRechargeSdk(customerService, UserId, amount, referrer, promocode, callBackBaseurl);
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("ORDER_ID", "");
        data.put("logRequestJson", "");
        data.put("message", "!!!coming soon!!!");
        data.put("error", true);

        return data;
    }

}
